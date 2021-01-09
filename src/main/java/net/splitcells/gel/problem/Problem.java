package net.splitcells.gel.problem;

import net.splitcells.gel.data.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.solution.Solution;

public interface Problem extends Piešķiršanas, ProblemView {
	Solution uzAtrisinājumu();

	Solution kāAtrisinājums();
}
