package wycli.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import wycli.cfg.Configuration;
import wycli.lang.Command;
import wycli.lang.Package;
import wycli.lang.Semantic;
import wycc.lang.Content;
import wycc.lang.Path;
import wycc.util.ZipFile;

public class RemotePackageRepository extends LocalPackageRepository {

	public static final Path REPOSITORY_URL = Path.fromString("repository/url");
	public static final Path REPOSITORY_ROUTE = Path.fromString("repository/route");
	public static final Path REPOSITORY_COOKIE = Path.fromString("repository/cookie");
	public static final Path REPOSITORY_PROXY = Path.fromString("repository/proxy");

	/**
	 * Schema for global configuration (i.e. which applies to all projects for a given user).
	 */
	public static Configuration.Schema SCHEMA = Configuration.fromArray(
			Configuration.UNBOUND_STRING(REPOSITORY_URL, "remote url", false),
			Configuration.UNBOUND_STRING(REPOSITORY_ROUTE, "remote route (template)", false),
			Configuration.UNBOUND_STRING(REPOSITORY_COOKIE, "remote cookie (for authentication)", false),
			Configuration.UNBOUND_STRING(REPOSITORY_PROXY, "proxy URL", false));

	/**
	 * The route defines a template from which to construct the complete url to the
	 * given package. Specifically, the variables <code>${NAME}$</code> and
	 * <code>${VERSION}</code> are replaced accordingly.
	 */
	private String pkgRoute = "/${NAME}/${VERSION}/${NAME}-v${VERSION}.zip";
	/**
	 * The index route defines the route to the package index.
	 */
	private String indexRoute = "/index.txt";
	/**
	 * The URI defines the base location from which to construct the complete URL to
	 * request the package from.
	 */
	private String uri = "https://github.com/Whiley/Repository/raw/master";
	/**
	 * The Cookie (if given) will be added to all HTTP requests.
	 */
	private String cookie = null;
	/**
	 * The proxy (if given) will be configured.
	 */
	private String proxy = null;
	/**
	 * Master index of all known semantic versions
	 */
	private Map<String,Set<Semantic.Version>> index = null;

	public RemotePackageRepository(Command.Environment environment, Content.Root root) throws IOException {
		this(environment, null, root);
	}

	public RemotePackageRepository(Command.Environment environment, Package.Repository parent, Content.Root root) throws IOException {
		super(environment, parent, root);
//		// Check whether URL configuration given
//		if(environment.hasKey(REPOSITORY_URL)) {
//			this.uri = environment.get(Value.UTF8.class, REPOSITORY_URL).toString();
//		}
//		// Check whether route configuration given
//		if(environment.hasKey(REPOSITORY_ROUTE)) {
//			this.pkgRoute = environment.get(Value.UTF8.class, REPOSITORY_ROUTE).toString();
//		}
//		// Check whether cookie configuration given
//		if(environment.hasKey(REPOSITORY_COOKIE)) {
//			this.cookie = environment.get(Value.UTF8.class, REPOSITORY_COOKIE).toString();
//		}
//		// Check whether proxy configuration given
//		if(environment.hasKey(REPOSITORY_PROXY)) {
//			this.proxy = environment.get(Value.UTF8.class, REPOSITORY_PROXY).toString();
//		}
	}

	@Override
	public Set<Semantic.Version> list(String pkg) throws IOException {
		Set<Semantic.Version> results = super.list(pkg);
		// Make sure index is upto date
		loadIndex();
		// Add any known versions from remote index
		if(index.containsKey(pkg)) {
			results.addAll(index.get(pkg));
		}
		// Done
		return results;
	}

	@Override
	public ZipFile get(String name, Semantic.Version version) throws IOException {
		// Check for local version of this package
		ZipFile pkg = super.get(name, version);
		// Did we find it?
		if (pkg == null) {
			// Nope, so get from remote
			ZipFile zf = getRemote(name, version);
			//
			if (zf != null) {
				// Store in local repository
				super.put(zf, name, version);
				// Read it out
				return super.get(name, version);
			}
		}
		// Done
		return pkg;
	}

	@Override
	public void put(ZipFile pkg, String name, Semantic.Version version) throws IOException {
		// FIXME: this is really a temporary hack.
		super.put(pkg, name, version);
	}

	/**
	 * Attempt to load a given package from the remote repository. This may fail if
	 * no such package exists.
	 *
	 * @param name
	 * @param version
	 * @return
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	private ZipFile getRemote(String name, Semantic.Version version) throws UnsupportedOperationException, IOException {
		String url = uri + pkgRoute.replace("${NAME}", name).replace("${VERSION}", version.toString());
		//
		CloseableHttpClient httpclient = getClient();
		// NOTE: connection pooling might be a better idea for performance reasons.
		HttpGet httpget = new HttpGet(url);
		// Configure get request (if necessary)
		if(cookie != null) {
			httpget.addHeader("Cookie", cookie);
		}
		// Now perform the request
		CloseableHttpResponse response = httpclient.execute(httpget);
		try {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				environment.getLogger().logTimedMessage("Downloaded " + url, 0, 0);
				return new ZipFile(root.getContentRegistry(), response.getEntity().getContent());
			} else {
				environment.getLogger().logTimedMessage("Failed downloading " + url, 0, 0);
				return null;
			}
		} finally {
			response.close();
		}
	}

	/**
	 * Load the package repository index.  That is, the mapping of all known packages to their available versions.
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private void loadIndex() throws IOException {
		if (index == null) {
			String url = uri + indexRoute;
			try {
				//
				CloseableHttpClient httpclient = getClient();
				// NOTE: connection pooling might be a better idea for performance reasons.
				HttpGet httpget = new HttpGet(url);
				// Configure get request (if necessary)
				if (cookie != null) {
					httpget.addHeader("Cookie", cookie);
				}
				// Now perform the request
				CloseableHttpResponse response = httpclient.execute(httpget);
				try {
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						// Index file downloaded, so parse it!
						environment.getLogger().logTimedMessage("Downloaded " + url, 0, 0);
						index = parseIndexFile(
								new BufferedReader(new InputStreamReader(response.getEntity().getContent())));
					} else {
						environment.getLogger().logTimedMessage("Failed downloading " + url, 0, 0);
						// Mark the index as empty
						this.index = Collections.emptyMap();
					}
				} finally {
					response.close();
				}
			} catch (UnknownHostException e) {
				environment.getLogger().logTimedMessage("Failed downloading " + url, 0, 0);
				// Mark the index as empty
				this.index =  Collections.emptyMap();
			}
		}
	}

	/**
	 * Create an appropriate client which takes into considerable any relevant
	 * configuration parameters.
	 *
	 * @return
	 */
	private CloseableHttpClient getClient() {
		// Configure proxy host (if applicable)
		if (this.proxy != null) {
			HttpHost proxyhost;
			String[] parts = proxy.split(":");
			// Decide whether port number given
			if (parts.length > 1) {
				proxyhost = new HttpHost(parts[0], Integer.parseInt(parts[1]));
			} else {
				proxyhost = new HttpHost(proxy);
			}
			// Done
			return HttpClients.custom().setProxy(proxyhost).build();
		} else {
			return HttpClients.createDefault();
		}
	}

	private static Map<String, Set<Semantic.Version>> parseIndexFile(BufferedReader reader) throws IOException {
		HashMap<String, Set<Semantic.Version>> result = new HashMap<>();
		while (reader.ready()) {
			String line = reader.readLine();
			String[] components = line.split("/");
			if (components.length == 2) {
				String pkg = components[0];
				Set<Semantic.Version> versions = result.get(pkg);
				if (versions == null) {
					versions = new HashSet<>();
					result.put(pkg, versions);
				}
				versions.add(new Semantic.Version(components[1]));
			}
		}
		return result;
	}
}
