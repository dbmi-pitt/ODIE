package edu.pitt.dbmi.odie.ui.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

/**
 * Class for serializing gadgets to/from a byte array
 */
public class SuggestionTransfer extends ByteArrayTransfer {
	private static SuggestionTransfer instance = new SuggestionTransfer();
	private static final String TYPE_NAME = "suggestion-transfer-format";
	private static final int TYPEID = registerType(TYPE_NAME);

	/**
	 * Returns the singleton gadget transfer instance.
	 */
	public static SuggestionTransfer getInstance() {
		return instance;
	}

	/**
	 * Avoid explicit instantiation
	 */
	private SuggestionTransfer() {
	}

	protected Suggestion[] fromByteArray(byte[] bytes) {
		DataInputStream in = new DataInputStream(
				new ByteArrayInputStream(bytes));

		try {
			/* read number of Suggestions */
			int n = in.readInt();
			/* read Suggestions */
			Suggestion[] Suggestions = new Suggestion[n];
			for (int i = 0; i < n; i++) {
				Suggestion Suggestion = readSuggestion(null, in);
				if (Suggestion == null) {
					return null;
				}
				Suggestions[i] = Suggestion;
			}
			return Suggestions;
		} catch (IOException e) {
			return null;
		}
	}

	/*
	 * Method declared on Transfer.
	 */
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	/*
	 * Method declared on Transfer.
	 */
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	/*
	 * Method declared on Transfer.
	 */
	protected void javaToNative(Object object, TransferData transferData) {
		byte[] bytes = toByteArray((Suggestion[]) object);
		if (bytes != null)
			super.javaToNative(bytes, transferData);
	}

	/*
	 * Method declared on Transfer.
	 */
	protected Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		return fromByteArray(bytes);
	}

	/**
	 * Reads and returns a single Suggestion from the given stream.
	 */
	private Suggestion readSuggestion(Suggestion parent, DataInputStream dataIn)
			throws IOException {
		/**
		 * Suggestion serialization format is as follows: (id) id of the
		 * Suggestion (id) of the analysis for the suggestion
		 */
		Suggestion s;

		long suggestionId = dataIn.readLong();
		long analysisId = dataIn.readLong();

		return GeneralUtils.getSuggestion(suggestionId, analysisId);

	}

	protected byte[] toByteArray(Suggestion[] Suggestions) {
		/**
		 * Transfer data is an array of Suggestions. Serialized version is:
		 * (int) number of Suggestions (Suggestion) Suggestion 1 (Suggestion)
		 * Suggestion 2 ... repeat for each subsequent Suggestion see
		 * writeSuggestion for the (Suggestion) format.
		 */
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOut);

		byte[] bytes = null;

		try {
			/* write number of markers, this needs to be changed if we want 
			 * to add all the suggestions under an aggregate suggestion. Currently
			 * it assumes that only the first suggestion for an aggregate suggestion
			 * will be added */
			out.writeInt(Suggestions.length);

			/* write markers */
			for (int i = 0; i < Suggestions.length; i++) {
				if (((Suggestion) Suggestions[i]).isAggregate()) {
					
					List<Suggestion> slist = GeneralUtils
							.getSuggestionsForAggregate((Suggestion) Suggestions[i]);

					
					for (Suggestion s : slist) {
						writeSuggestion(s, out);
						break; //write only first one
					}
				} else
					writeSuggestion((Suggestion) Suggestions[i], out);
			}
			out.close();
			bytes = byteOut.toByteArray();
		} catch (IOException e) {
			// when in doubt send nothing
		}
		return bytes;
	}

	/**
	 * Writes the given Suggestion to the stream.
	 */
	private void writeSuggestion(Suggestion suggestion, DataOutputStream dataOut)
			throws IOException {
		/**
		 * Suggestion serialization format is as follows: (Long) object id of
		 * suggestion (Long) analysis id
		 */
		dataOut.writeLong(suggestion.getId());
		dataOut.writeLong(suggestion.getAnalysis().getId());
	}
}