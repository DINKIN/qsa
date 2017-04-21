package fragments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pdb.SimpleStructure;

/**
 *
 * @author Antonin Pavelka
 */
public final class FragmentsFactory implements Serializable {

	private static final long serialVersionUID = 1L;
	private Parameters params_ = Parameters.create();
	private static boolean print = false;

	public FragmentsFactory() {
	}

	public Fragments create(SimpleStructure ss, int wordLength, int sparsity) {
		WordsFactory wf = new WordsFactory(ss, wordLength);
		wf.setSparsity(sparsity);
		Words words = wf.create();
		if (print) {
			System.out.println("***** " + ss.size());
			for (WordImpl w : words) {
				w.print();
			}
		}
		WordImpl[] wa = words.toArray();
		List<Fragment> fl = new ArrayList<>();
		for (int xi = 0; xi < wa.length; xi++) {
			for (int yi = 0; yi < xi; yi++) {
				WordImpl x = wa[xi];
				WordImpl y = wa[yi];
				if (x.isInContact(y, params_.getResidueContactDistance())) {
					Fragment f = new Fragment(x, y);
					fl.add(f);
					fl.add(f.switchWords());
				}
			}
		}
		Fragment[] fa = new Fragment[fl.size()];
		fl.toArray(fa);
		Fragments fs = new Fragments(ss, fa, wa);
		return fs;
	}
}
