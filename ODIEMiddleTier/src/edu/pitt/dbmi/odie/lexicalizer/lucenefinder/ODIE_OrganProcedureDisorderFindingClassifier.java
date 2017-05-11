package edu.pitt.dbmi.odie.lexicalizer.lucenefinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.pitt.terminology.lexicon.SemanticType;

public class ODIE_OrganProcedureDisorderFindingClassifier {

	private static final String[] umlsSemanticTypesToTuiTable = {
			" Acquired Abnormality                      ", " T020 ",
			" Activity                                  ", " T052 ",
			" Age Group                                 ", " T100 ",
			" Amino Acid Sequence                       ", " T087 ",
			" Amino Acid, Peptide, or Protein           ", " T116 ",
			" Amphibian                                 ", " T011 ",
			" Anatomical Abnormality                    ", " T190 ",
			" Anatomical Structure                      ", " T017 ",
			" Animal                                    ", " T008 ",
			" Antibiotic                                ", " T195 ",
			" Archaeon                                  ", " T194 ",
			" Bacterium                                 ", " T007 ",
			" Behavior                                  ", " T053 ",
			" Biologic Function                         ", " T038 ",
			" Biologically Active Substance             ", " T123 ",
			" Biomedical Occupation or Discipline       ", " T091 ",
			" Biomedical or Dental Material             ", " T122 ",
			" Bird                                      ", " T012 ",
			" Body Location or Region                   ", " T029 ",
			" Body Part, Organ, or Organ Component      ", " T023 ",
			" Body Space or Junction                    ", " T030 ",
			" Body Substance                            ", " T031 ",
			" Body System                               ", " T022 ",
			" Carbohydrate                              ", " T118 ",
			" Carbohydrate Sequence                     ", " T088 ",
			" Cell                                      ", " T025 ",
			" Cell Component                            ", " T026 ",
			" Cell Function                             ", " T043 ",
			" Cell or Molecular Dysfunction             ", " T049 ",
			" Chemical                                  ", " T103 ",
			" Chemical Viewed Functionally              ", " T120 ",
			" Chemical Viewed Structurally              ", " T104 ",
			" Classification                            ", " T185 ",
			" Clinical Attribute                        ", " T201 ",
			" Clinical Drug                             ", " T200 ",
			" Conceptual Entity                         ", " T077 ",
			" Congenital Abnormality                    ", " T019 ",
			" Daily or Recreational Activity            ", " T056 ",
			" Diagnostic Procedure                      ", " T060 ",
			" Disease or Syndrome                       ", " T047 ",
			" Drug Delivery Device                      ", " T203 ",
			" Educational Activity                      ", " T065 ",
			" Eicosanoid                                ", " T111 ",
			" Element, Ion, or Isotope                  ", " T196 ",
			" Embryonic Structure                       ", " T018 ",
			" Entity                                    ", " T071 ",
			" Environmental Effect of Humans            ", " T069 ",
			" Enzyme                                    ", " T126 ",
			" Eukaryote                                 ", " T204 ",
			" Event                                     ", " T051 ",
			" Experimental Model of Disease             ", " T050 ",
			" Family Group                              ", " T099 ",
			" Finding                                   ", " T033 ",
			" Fish                                      ", " T013 ",
			" Food                                      ", " T168 ",
			" Fully Formed Anatomical Structure         ", " T021 ",
			" Functional Concept                        ", " T169 ",
			" Fungus                                    ", " T004 ",
			" Gene or Genome                            ", " T028 ",
			" Genetic Function                          ", " T045 ",
			" Geographic Area                           ", " T083 ",
			" Governmental or Regulatory Activity       ", " T064 ",
			" Group                                     ", " T096 ",
			" Group Attribute                           ", " T102 ",
			" Hazardous or Poisonous Substance          ", " T131 ",
			" Health Care Activity                      ", " T058 ",
			" Health Care Related Organization          ", " T093 ",
			" Hormone                                   ", " T125 ",
			" Human                                     ", " T016 ",
			" Human-caused Phenomenon or Process        ", " T068 ",
			" Idea or Concept                           ", " T078 ",
			" Immunologic Factor                        ", " T129 ",
			" Indicator, Reagent, or Diagnostic Aid     ", " T130 ",
			" Individual Behavior                       ", " T055 ",
			" Injury or Poisoning                       ", " T037 ",
			" Inorganic Chemical                        ", " T197 ",
			" Intellectual Product                      ", " T170 ",
			" Laboratory or Test Result                 ", " T034 ",
			" Laboratory Procedure                      ", " T059 ",
			" Language                                  ", " T171 ",
			" Lipid                                     ", " T119 ",
			" Machine Activity                          ", " T066 ",
			" Mammal                                    ", " T015 ",
			" Manufactured Object                       ", " T073 ",
			" Medical Device                            ", " T074 ",
			" Mental or Behavioral Dysfunction          ", " T048 ",
			" Mental Process                            ", " T041 ",
			" Molecular Biology Research Technique      ", " T063 ",
			" Molecular Function                        ", " T044 ",
			" Molecular Sequence                        ", " T085 ",
			" Natural Phenomenon or Process             ", " T070 ",
			" Neoplastic Process                        ", " T191 ",
			" Neuroreactive Substance or Biogenic Amine ", " T124 ",
			" Nucleic Acid, Nucleoside, or Nucleotide   ", " T114 ",
			" Nucleotide Sequence                       ", " T086 ",
			" Occupation or Discipline                  ", " T090 ",
			" Occupational Activity                     ", " T057 ",
			" Organ or Tissue Function                  ", " T042 ",
			" Organic Chemical                          ", " T109 ",
			" Organism                                  ", " T001 ",
			" Organism Attribute                        ", " T032 ",
			" Organism Function                         ", " T040 ",
			" Organization                              ", " T092 ",
			" Organophosphorus Compound                 ", " T115 ",
			" Pathologic Function                       ", " T046 ",
			" Patient or Disabled Group                 ", " T101 ",
			" Pharmacologic Substance                   ", " T121 ",
			" Phenomenon or Process                     ", " T067 ",
			" Physical Object                           ", " T072 ",
			" Physiologic Function                      ", " T039 ",
			" Plant                                     ", " T002 ",
			" Population Group                          ", " T098 ",
			" Professional or Occupational Group        ", " T097 ",
			" Professional Society                      ", " T094 ",
			" Qualitative Concept                       ", " T080 ",
			" Quantitative Concept                      ", " T081 ",
			" Receptor                                  ", " T192 ",
			" Regulation or Law                         ", " T089 ",
			" Reptile                                   ", " T014 ",
			" Research Activity                         ", " T062 ",
			" Research Device                           ", " T075 ",
			" Self-help or Relief Organization          ", " T095 ",
			" Sign or Symptom                           ", " T184 ",
			" Social Behavior                           ", " T054 ",
			" Spatial Concept                           ", " T082 ",
			" Steroid                                   ", " T110 ",
			" Substance                                 ", " T167 ",
			" Temporal Concept                          ", " T079 ",
			" Therapeutic or Preventive Procedure       ", " T061 ",
			" Tissue                                    ", " T024 ",
			" Vertebrate                                ", " T010 ",
			" Virus                                     ", " T005 ",
			" Vitamin                                   ", " T127 " };

	private static final Map<String, String> umlsSemanticTypesToTuiMap = new HashMap<String, String>();

	private static final String[] disorderTuis = { "T019", "T020", "T037",
			"T046", "T047", "T048", "T049", "T050", "T190", "T191" };
	private static final String[] findingTuis = { "T033", "T034", "T040",
			"T041", "T042", "T043", "T044", "T045", "T046", "T056", "T057",
			"T184" };
	private static final String[] organTuis = { "T021", "T022", "T023", "T024",
			"T025", "T026", "T029", "T030" };
	private static final String[] procedureTuis = { "T059", "T060", "T061" };

	private static final ArrayList<String> organTuiArray = new ArrayList<String>();
	private static final ArrayList<String> procedureTuiArray = new ArrayList<String>();
	private static final ArrayList<String> disorderTuiArray = new ArrayList<String>();
	private static final ArrayList<String> findingTuiArray = new ArrayList<String>();

	private static ODIE_OrganProcedureDisorderFindingClassifier singleton = null;

	static {

		List<String> umlsSemanticTypesToTuiList = Arrays
				.asList(umlsSemanticTypesToTuiTable);

		for (Iterator<String> iterator = umlsSemanticTypesToTuiList.iterator(); iterator
				.hasNext();) {
			String sty = iterator.next();
			String tui = iterator.next();
			sty = sty.replaceAll("^\\s*", "").replaceAll("\\s*$", "");
			tui = tui.replaceAll("^\\s*", "").replaceAll("\\s*$", "");
			System.out.println(sty + " ==> " + tui);
			umlsSemanticTypesToTuiMap.put(sty, tui);
		}

		organTuiArray.addAll(Arrays.asList(organTuis));
		procedureTuiArray.addAll(Arrays.asList(procedureTuis));
		disorderTuiArray.addAll(Arrays.asList(disorderTuis));
		findingTuiArray.addAll(Arrays.asList(findingTuis));
	}

	private int semanticType = -1;
	private String spaceSeparatedTuis = "";

	public static ODIE_OrganProcedureDisorderFindingClassifier getInstance() {
		if (singleton == null) {
			singleton = new ODIE_OrganProcedureDisorderFindingClassifier();
		}
		return singleton;
	}

	private ODIE_OrganProcedureDisorderFindingClassifier() {
		;
	}

	public void processLexicalizerCls(
			ODIE_LexicalizerClsWrapper odieLexicalizerCls) {
		int result = -1;
		final HashSet<String> tuis = new HashSet<String>();
		SemanticType[] semanticTypeArray = odieLexicalizerCls.getCls()
				.getConcept().getSemanticTypes();
		if (semanticTypeArray != null && semanticTypeArray.length > 0) {
			for (SemanticType semanticType : semanticTypeArray) {
				String sty = semanticType.getName();
				if (sty == null) {
					continue;
				}
				String tui = umlsSemanticTypesToTuiMap.get(sty);
				if (tui == null) {
					;
				} else {
					tuis.add(tui);
					if (organTuiArray.contains(tui)) {
						result = 1;
					} else if (procedureTuiArray.contains(tui)) {
						result = 2;
					} else if (disorderTuiArray.contains(tui)) {
						result = 3;
					} else if (findingTuiArray.contains(tui)) {
						result = 4;
					}
				}
			}
			setSpaceSeparatedTuis(glob(tuis, " "));
		}
		setSemanticType(result);

	}

	public String glob(Collection<String> words, String separator) {
		StringBuffer commaSeparatedWords = new StringBuffer();
		for (String word : words) {
			commaSeparatedWords.append(word);
			commaSeparatedWords.append(separator);
		}
		if (commaSeparatedWords.length() > 0) {
			commaSeparatedWords.deleteCharAt(commaSeparatedWords.length() - 1);
		}
		return commaSeparatedWords.toString();
	}

	public int getSemanticType() {
		return semanticType;
	}

	public void setSemanticType(int semanticType) {
		this.semanticType = semanticType;
	}

	public String getSpaceSeparatedTuis() {
		return spaceSeparatedTuis;
	}

	public void setSpaceSeparatedTuis(String spaceSeparatedTuis) {
		this.spaceSeparatedTuis = spaceSeparatedTuis;
	}

}
