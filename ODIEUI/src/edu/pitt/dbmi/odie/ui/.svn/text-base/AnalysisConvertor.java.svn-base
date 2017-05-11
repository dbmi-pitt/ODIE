package edu.pitt.dbmi.odie.ui;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;

public class AnalysisConvertor extends AbstractParameterValueConverter {

	public AnalysisConvertor() {
	}

	@Override
	public Object convertToObject(String parameterValue)
			throws ParameterValueConversionException {

		MiddleTier mt = Activator.getDefault().getMiddleTier();

		Analysis a = mt.getAnalysisForName(parameterValue);

		if (a == null)
			throw new ParameterValueConversionException(parameterValue);
		else
			return a;
	}

	@Override
	public String convertToString(Object parameterValue)
			throws ParameterValueConversionException {
		if (parameterValue instanceof Analysis)
			return ((Analysis) parameterValue).getName();
		else
			throw new ParameterValueConversionException(parameterValue
					.getClass().getName());
	}

}
