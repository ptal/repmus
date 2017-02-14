package bonsai;

import java.util.*;
import inria.meije.rc.sugarcubes.*;
import inria.meije.rc.sugarcubes.implementation.*;
import org.chocosolver.solver.variables.*;
import org.chocosolver.solver.constraints.nary.alldifferent.*;
import org.chocosolver.solver.*;
import bonsai.runtime.core.*;
import bonsai.runtime.choco.*;
import bonsai.runtime.sugarcubes.*;
import bonsai.cp.core.*;

import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.RTree;
import projects.music.classes.music.RNote;
import projects.music.classes.music.RChord;
import projects.music.classes.music.Measure;
import kernel.tools.*;
import projects.music.classes.music.Voice;
import projects.music.classes.music.Voice.VoicePanel;
import projects.music.editors.MusicalEditor;

public class AIS3 implements Executable
{
  private world_line VarStore domains = bot;
  private world_line ConstraintStore constraints = bot;
  private single_time FlatLattice<Consistent> consistent = bot;

  private int n;
  private VoicePanel panel;
  private IntVar[] notes;

  public AIS3(int n, VoicePanel panel) {
    this.n = n;
    this.panel = panel;
    setupPanel();
  }

  public proc execute() {
    model();
    trap FoundSolution {
      engine();
    }
    ~printVariables("Solution", consistent, domains);
  }

  proc model() {
    ~modelChoco(domains, constraints);
    ~printModel("After initialization", consistent, domains);
  }

  proc engine() {
    module Branching branching = Branching.firstFailMiddle(domains.model());
    module Propagation propagation = new Propagation();
    par
    || run branching.split();
    || run propagation;
    || displaySolution();
    end
  }

  proc displaySolution() {
    loop {
      ~updatePanel();
      pause;
    }
  }

  private void modelChoco(VarStore domains,
    ConstraintStore constraints)
  {
    Model model = domains.model();
    notes = new IntVar[n];
    IntVar[] intervales = new IntVar[n];
    for(int i = 0; i < n; i++) {
      notes[i] = (IntVar) domains.alloc(new IntDomain(0, n-1));
      intervales[i] = (IntVar) domains.alloc(new IntDomain(0, n-1));
    }
    constraints.join(new AllDifferent(notes, "BC"));
    constraints.join(new AllDifferent(intervales, "BC"));
    constraints.join(model.arithm(notes[0], "=", 0));
    for (int i=0; i < n-1; i++){
      constraints.join(model.distance(notes[i + 1], notes[i], "=", intervales[i]));
    }
  }

  private static void printHeader(String message,
    FlatLattice<Consistent> consistent)
  {
    System.out.print("["+message+"][" + consistent + "]");
  }

  private static void printModel(String message,
    FlatLattice<Consistent> consistent, VarStore domains)
  {
    printHeader(message, consistent);
    System.out.print(domains.model());
  }

  private static void printVariables(String message,
    FlatLattice<Consistent> consistent, VarStore domains)
  {
    printHeader(message, consistent);
    System.out.print(" Variables = [");
    for (IntVar v : domains.vars()) {
      System.out.print(v + ", ");
    }
    System.out.println("]");
  }

  private void setupPanel() {
    MusicalEditor ed = (MusicalEditor) panel.getEditor();
    Voice object;
    if (ed != null) {
      object = (Voice) ed.getObject();
      List<List<Integer>> midics = new ArrayList<List<Integer>>();
      List<RChord> rep1 = new ArrayList<RChord> ();
      RChord chord = new RChord (ST.createList(1, 6000),
              ST.createList(1, 100), ST.createList(1, (long) 0), new Fraction (1), ST.createList(1, 1), 60);
      rep1.add(chord);
      List<RTree> rep = new ArrayList<RTree>();
      rep.add(new RTree(1,null));
      rep.add(new RTree(1,null));
      rep.add(new RTree(1,null));
      rep.add(new RTree(1,null));
      rep.add(new RTree(1,null));
      rep.add(new RTree(1,null));
      rep.add(new RTree(1,null));
      rep.add(new RTree(1,null));
      rep.add(new RTree(1,null));
      rep.add(new RTree(1,null));
      rep.add(new RTree(1,null));
      rep.add(new RTree(1,null));
      RTree onert = new RTree(new Fraction (12,4),rep);
      List<RTree> mes = new ArrayList<RTree>();
      mes.add(onert);
      object.fillVoice (mes, rep1, 60);
      for (MusicalObject meas : object.getElements())
        for (MusicalObject ch : ((Measure) meas).getElements())
          for (MusicalObject note : ((RChord) ch).getElements()) {
            List<Integer> dom = new ArrayList<Integer> ();
            dom.add(6000);dom.add(7200);
            ((RNote) note).setDom(dom);
        }
    }
  }

  private void updatePanel() {
    MusicalEditor ed = (MusicalEditor) panel.getEditor();
    Voice object;
    object = (Voice) ed.getObject();
    int i = 0;
    for (MusicalObject meas : object.getElements()) {
      for (MusicalObject ch : ((Measure) meas).getElements()) {
        for (MusicalObject note : ((RChord) ch).getElements()) {
          if (notes[i].isInstantiated()) {
            ((RNote) note).setMidic(6000 + notes[i].getValue() * 100);
            ((RNote) note).setDom(null);
          }
          else {
            List<Integer> dom = new ArrayList<Integer> ();
            dom.add(6000 + notes[i].getLB() * 100);dom.add(6000 + notes[i].getUB() * 100);
            ((RNote) note).setDom(dom);
          }
          i++;
        }
      }
    }
    if (ed != null) {
      panel.updatePanel(true);
    }
  }
}
