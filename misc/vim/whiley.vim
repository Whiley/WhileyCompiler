"Vim syntax file
"Language: Whiley
"Maintainer: Connor Findlay
"Latest Revision: 4 June 2013
"Support for trailing space highlighting and tab vs. space highlighting can be turned
"on and off with the following commands:
"			:let whiley_space_error_highlighting=1
"			:set syntax=whiley
" to turn off:
"			:unlet whiley_space_error_highlighting
"			:set syntax=whiley

"Check to see if a filetype syntax is already defined.
if exists("b:current_syntax")
	finish
endif

"----------------------------------------------------------------------------
"Keywords
"Syntax of this is:
"syn keyword syntaxElementKeyword keyword1 keyword2 nextgroup=syntaxElement2
"----------------------------------------------------------------------------

syn keyword whileyExternal native
syn keyword whileyLabel	default
syn keyword whileyPrimitive real int bool void string char void ref
syn keyword whileyBooleanLiteral true false
syn keyword whileyConstant null
syn keyword whileyStatement return assert
syn keyword whileyExceptions throw throws catch try
syn keyword whileyLoop	do while for in
syn keyword whileyOperator is as new
syn keyword whileyScope	public protected private
syn keyword whileyTypedef this
syn keyword whileyKeyword export extern is assume from where ensures requires str in no some all
syn keyword whileyConditional if else switch break skip
syn keyword whileyTodo contained TODO FIXME XXX

"Not how it's used in the language, but gives visual seperation.
syn keyword whileyMacro	define

"---------------------------------------------------
"Matches
"Syntax of this is:
"syn match syntaxElementMatch 'regexp' contains=syntaxElement1
"nextGroup=syntaxElement2
"---------------------------------------------------

syn match whileyExternal	"\<package\>\?"
syn match whileyNumber 		"\<[0-9][0-9]*"
syn match specialChar	 	contained "\\\([4-9]\d\|[0-3]\d\d\|[\"\\'ntbrf]\|u\x\{4\}\)"
syn match   whileyCommentStar	 contained "^\s*\*[^/]"me=e-1
syn match   whileyCommentStar	 contained "^\s*\*$"
syn match   whileyLineComment	 "//.*" contains=whileyTodo


"Highlight trailing whitespace, and mixed tabs and spaces. Taken from Python syntax file,
" distributed with vim 7.3
"Will highlight spaces mixed with tabs and trailing spaces. Enable with:
" :let whiley_space_error_highlight=1

if exists("whiley_space_error_highlight")
	syn match   spaceError	display excludenl "\s\+$"
	syn match   spaceError  display excludenl "\s\+$"
	syn match   spaceError	display " \+\t"
	syn match   spaceError	display "\t\+ "
endif


"Match the import statement
syn match 	whileyExternal "\<import\>\?"

"Find the delimeters in the text
syn match 	whileyDelimiter contained "[,;]"

"Character literals
syn match   whileySpecialCharError contained "[^']"
syn match   whileyCharacter	 "'[^']*'" contains=SpecialChar,whileySpecialCharError
syn match   whileyCharacter	 "'\\''" contains=SpecialChar
syn match   whileyCharacter	 "'[^\\]'"

"---------------------------------------------------
"Regions
"syn region syntaxElementRegion start='x' end='y'
"---------------------------------------------------

syn region  whileyString		start=+"+ end=+"+ end=+$+ contains=specialChar
syn region 	whileyList			start=+\[+ end=+\]+ end=+$+
syn region  whileyList   		start="\[" end="\]" contains=whileyNumber, whileyString
syn region  whileyLabel			matchgroup=javaLabel start="\<case\>" end=":"
syn region  whileyCharLiteral	start=/'/ end=/'/ end=+$+ contains=whileyCharacter oneline
syn region  whileySet 			start=/{/ end=/}/ contains=whileySet, whileyNumber
syn region  whileyComment			start="/\*"  end="\*/" contains=whileyTodo

"Let vim know that we have a style now, and that the style is whiley.
let b:current_syntax = "whiley"

"-----------------------------------------------------------------------------
"The actual highlighty bit, where we tell vim how to, and what to, highlight.
"Syntax of this is:
"hi def link syntaxElement vimSyntaxType
"-----------------------------------------------------------------------------

if exists("whiley_space_error_highlight")
	hi def link spaceError				Error
endif

hi def link whileyScope 			StorageClass
hi def link whileyConditional		Conditional
hi def link whileyCharacter 		Character
hi def link whileyDelimiter 		Delimeter
hi def link whileySet 				Delimeter
hi def link whileyExceptions 		Exception
hi def link whileyStatement			Statement
hi def link whileyConstant			Constant
hi def link whileyOperator			Operator
hi def link whileyBooleanLiteral 	Boolean
hi def link whileyComment 			Comment
hi def link whileyCommentStar		Comment
hi def link whileyLineComment		Comment
hi def link whileyExternal 			Include
hi def link whileyKeyword			Keyword
hi def link whileyTypedef 			TypeDef
hi def link whileyNumber			Number
hi def link whileyLoop				Repeat
hi def link whileyList				String
hi def link whileyString			String
hi def link whileyLabel				Label
hi def link whileyMacro				Macro
hi def link whileyTodo				Todo
hi def link whileyPrimitive			Type