package pdb.cath;

import pdb.ResidueFilter;

/**
 *
 * @author Antonin Pavelka
 */
public class CathDomainResidueFilter implements ResidueFilter {

	private final CathDomain domain;

	public CathDomainResidueFilter(CathDomain domain) {
		this.domain = domain;
	}

	@Override
	public boolean reject(String pdbCode, String chain, int residueNumber, Character insertionCode) {
		return !domain.doesResidueBelong(pdbCode, chain, residueNumber, insertionCode);
	}
}
