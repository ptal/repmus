// Copyright 2017 Pierre Talbot (IRCAM)

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
import bonsai.runtime.core.*;
import bonsai.runtime.choco.*;
import bonsai.runtime.sugarcubes.*;

public class Statistics implements Executable
{
  public Program execute() 
  {
    return
      new SpacetimeVar("nodes", true, Spacetime.SingleSpace, true, 2, (env) -> new FlatLattice(),
      new SpacetimeVar("depth", true, Spacetime.WorldLine, true, 2, (env) -> new RFlatLattice(),
      SC.merge(
        countNode(),
        countDepth())));
  }

  public Program countDepth() 
  {
    return
      SC.seq(
        new Tell("depth", (env) -> new RInteger(0)),
        SC.loop(
          SC.seq(
            SC.stop(),
            new Tell("depth", (env) -> {
              RFlatLattice<RInteger> depth = (RFlatLattice<RInteger>) env.var("depth", 1);
              return inc(depth);}))));
  }

  public Program countNode() 
  {
    return
      SC.seq(
        new Tell("nodes", (env) -> new RInteger(1)),
        SC.loop(
          SC.seq(
            SC.stop(),
            new Tell("nodes", (env) -> {
              FlatLattice<RInteger> nodes = (FlatLattice<RInteger>) env.var("nodes", 1);
              return inc(nodes);}))));
  }

  public Program print() 
  {
    return
      new ClosureAtom((env) -> {
        RFlatLattice<RInteger> depth = (RFlatLattice<RInteger>) env.var("depth", 0);
        FlatLattice<RInteger> nodes = (FlatLattice<RInteger>) env.var("nodes", 0);
        printStats(nodes, depth);});
  }

  private RInteger inc(FlatLattice<RInteger> x) {
    int n = x.unwrap().value + 1;
    return new RInteger(n);
  }

  private void printStats(FlatLattice<RInteger> nodes, RFlatLattice<RInteger> depth) {
    System.out.println("Nodes: " + nodes);
    System.out.println("Current depth: " + depth);
  }

}
