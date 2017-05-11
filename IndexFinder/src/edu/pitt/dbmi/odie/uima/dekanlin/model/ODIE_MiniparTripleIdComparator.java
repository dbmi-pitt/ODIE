package edu.pitt.dbmi.odie.uima.dekanlin.model;

import java.util.Comparator;

public class ODIE_MiniparTripleIdComparator implements
		Comparator<ODIE_MiniparTriple> {

	public int compare(ODIE_MiniparTriple tripleOne,
			ODIE_MiniparTriple tripleTwo) {

		int idOne = tripleOne.id;
		int idTwo = tripleTwo.id;

		if (idOne > idTwo)

			return 1;

		else if (idOne < idTwo)

			return -1;

		else

			return 0;

	}

}
