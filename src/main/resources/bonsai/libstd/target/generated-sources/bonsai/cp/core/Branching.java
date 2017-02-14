// Copyright 2016 Pierre Talbot (IRCAM)

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package bonsai.cp.core;

import java.util.*;
import inria.meije.rc.sugarcubes.*;
import inria.meije.rc.sugarcubes.implementation.*;
import org.chocosolver.solver.*;
import org.chocosolver.solver.variables.*;
import org.chocosolver.solver.search.strategy.selectors.variables.*;
import org.chocosolver.solver.search.strategy.selectors.values.*;
import bonsai.runtime.core.*;
import bonsai.runtime.choco.*;
import bonsai.runtime.sugarcubes.*;

public class Branching implements Executable
{
  private VariableSelector<IntVar> var;
  private IntValueSelector val;
  public Program execute() 
  {
    return
      split();
  }

  public Program exclude() 
  {
    return
      SC.loop(
        SC.seq(
          SC.when(new EntailmentConfig("consistent", 0, (env) -> Consistent.Unknown),
            new SpacetimeVar("x", false, Spacetime.SingleTime, true, 1, (env) -> {
              VarStore domains = (VarStore) env.var("domains", 0);
              return var.getVariable(domains.vars());},
            new SpacetimeVar("v", false, Spacetime.SingleTime, true, 1, (env) -> {
              IntVar x = (IntVar) env.var("x", 0);
              return val.selectValue(x);},
            new Space(new ArrayList<>(Arrays.asList(
              new SpaceBranch(
                new Tell("constraints", (env) -> {
                  Integer v = (Integer) env.var("v", 0);
                  IntVar x = (IntVar) env.var("x", 0);
                  return x.eq(v);})),
              new SpaceBranch(
                new Tell("constraints", (env) -> {
                  Integer v = (Integer) env.var("v", 0);
                  IntVar x = (IntVar) env.var("x", 0);
                  return x.ne(v);}))))))),
            SC.nothing()),
          SC.stop()));
  }

  public Program split() 
  {
    return
      SC.loop(
        SC.seq(
          SC.when(new EntailmentConfig("consistent", 0, (env) -> Consistent.Unknown),
            new SpacetimeVar("x", false, Spacetime.SingleTime, true, 1, (env) -> {
              VarStore domains = (VarStore) env.var("domains", 0);
              return var.getVariable(domains.vars());},
            new SpacetimeVar("v", false, Spacetime.SingleTime, true, 1, (env) -> {
              IntVar x = (IntVar) env.var("x", 0);
              return val.selectValue(x);},
            new Space(new ArrayList<>(Arrays.asList(
              new SpaceBranch(
                new Tell("constraints", (env) -> {
                  Integer v = (Integer) env.var("v", 0);
                  IntVar x = (IntVar) env.var("x", 0);
                  return x.le(v);})),
              new SpaceBranch(
                new Tell("constraints", (env) -> {
                  Integer v = (Integer) env.var("v", 0);
                  IntVar x = (IntVar) env.var("x", 0);
                  return x.gt(v);}))))))),
            SC.nothing()),
          SC.stop()));
  }

  public static Branching firstFailMiddle(Model model) {
    return new Branching(new FirstFail(model), new IntDomainMiddle(true));
  }

  public static Branching inputOrderMin(Model model) {
    return new Branching(new InputOrder(model), new IntDomainMin());
  }

  public Branching(VariableSelector<IntVar> var, IntValueSelector val) {
    this.var = var;
    this.val = val;
  }

}
