// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyll.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wycc.util.Pair;
import wyll.core.LowLevel;
import wyll.core.LowLevel.Type;

/**
 * Provides various generic implementations of LowLevel.Type. The intention is
 * that this should cover the majority of use cases required by a backend. Only
 * in very strange circumstances would one want to use something else.
 *
 * @author David J. Pearce
 *
 */
public abstract class LowLevelType implements LowLevel.Type {

  public static class Void extends LowLevelType implements LowLevel.Type.Void {
    @Override
    public int getWidth() {
      return 0;
    }
  }

  public static class Null extends LowLevelType implements LowLevel.Type.Null {
    @Override
    public int getWidth() {
      return 0;
    }
  }

  public static class Bool extends LowLevelType implements LowLevel.Type.Bool {
    @Override
    public int getWidth() {
      return 1;
    }
  }

  public static class Int extends LowLevelType implements LowLevel.Type.Int {
    private final int bitwidth;

    public Int(int bitwidth) {
      this.bitwidth = bitwidth;
    }

    @Override
    public int getWidth() {
      return bitwidth;
    }
  }


  public static class Array extends LowLevelType implements LowLevel.Type.Array {
    /**
     * Identifies if this array has a fixed length, or -1 for unknown length arrays.
     */
    private final int length;

    /**
     * Element type of this array.
     */
    private final LowLevelType element;

    public Array(LowLevelType element) {
      this.length = -1;
      this.element = element;
    }

    public Array(int length, LowLevelType element) {
      this.length = length;
      this.element = element;
    }

    public boolean isStaticallySized() {
      return length != -1;
    }

    public int getLength() {
      return length;
    }

    @Override
    public int getWidth() {
      // For fixed-width array, this is a relatively easy calculuation. For
      // dynamically sized arrays, we need the underlying pointer type. Perhaps we can
      // get this from the enclosing class?
      throw new UnsupportedOperationException();
    }

    @Override
    public Type getElement() {
      return element;
    }
  }

  public static class Record extends LowLevelType implements LowLevel.Type.Record {
    private List<Pair<LowLevelType,String>> fields;

    public Record(List<Pair<LowLevelType,String>> fields) {
      this.fields = new ArrayList<>(fields);
    }

    @Override
    public int getWidth() {
      throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
      return fields.size();
    }

    @Override
    public Pair<? extends Type, String> getField(int i) {
      return fields.get(i);
    }
  }

  public static class Union extends LowLevelType implements LowLevel.Type.Union {
    private ArrayList<Type> elements;

    public Union(Collection<Type> elements) {
      this.elements = new ArrayList<>(elements);
    }

    @Override
    public int getWidth() {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public int size() {
      return elements.size();
    }

    @Override
    public Type getElement(int i) {
      return elements.get(i);
    }
  }

  public static class Method extends LowLevelType implements Type.Method {
    private final List<Type> parameters;
    private final Type returns;

    public Method(List<Type> parameters, Type returns) {
      this.parameters = new ArrayList<>(parameters);
      this.returns = returns;
    }

    @Override
    public int getWidth() {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public int numberOfParameters() {
      return parameters.size();
    }

    @Override
    public Type getParameterType(int param) {
      return parameters.get(param);
    }

    @Override
    public boolean hasReturnType() {
      return returns != null;
    }

    @Override
    public Type getReturnType() {
      return returns;
    }
  }

  public static class Reference extends LowLevelType implements Type.Reference {
    /**
     * Element type of this array.
     */
    private final LowLevelType element;

    public Reference(LowLevelType element) {
      this.element = element;
    }

    @Override
    public int getWidth() {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public Type getElement() {
      return element;
    }

  }

  public static class Recursive extends LowLevelType implements Type.Recursive {

    @Override
    public int getWidth() {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public String getName() {
      // TODO Auto-generated method stub
      return null;
    }

  }
}