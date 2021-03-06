package global.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import global.FlexibleLogger;
import java.nio.file.Files;
import java.nio.file.Path;
import pdb.StructureSource;
import util.Pair;

/**
 * Directory structure is home/job/task, where home contains global files, job outputs of a single run and task are
 * directories for each single search or pairwise comparison.
 */
public class Directories {
	
	private File job;
	private File task;
	private File structures;
	private final File home;
	
	public Directories(File home) {
		System.out.println("Using home directory " + home.getAbsolutePath() + "*");
		this.home = home;
	}
	
	public void createDirs(Path p) {
		try {
			if (!Files.exists(p)) {
				System.out.println("Creating " + p);
				Files.createDirectories(p);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public File getHome() {
		return home;
	}
	
	public Path getHomePath() {
		return home.toPath();
	}
	
	public File getTask() {
		if (task == null || !task.exists()) {
			throw new RuntimeException("Task directory problem: " + task);
		}
		return task;
	}
	
	public File getJob() {
		if (job == null || !job.exists()) {
			throw new RuntimeException("Job directory problem: " + job);
		}
		return job;
	}
	
	private Path getJobs() {
		Path p = getHome().toPath().resolve("jobs");
		createDirs(p);
		return p;
	}
	
	public void createJob() {
		job = createNextUniqueDir(getJobs().toFile(), "job", "");
	}
	
	public void createTask(String prefix) {
		task = createNextUniqueDir(getTasks(), prefix, "");
	}
	
	public File getTasks() {
		Path p = getJob().toPath().resolve("tasks");
		createDirs(p);
		return p.toFile();
	}
	
	private File createNextUniqueDir(File parrent, String prefix, String nameStart) {
		int max = 0;
		for (File f : parrent.listFiles()) {
			if (f.getName().startsWith(prefix)) {
				StringTokenizer st = new StringTokenizer(f.getName(), "_");
				String last = "";
				while (st.hasMoreTokens()) {
					last = st.nextToken();
				}
				int i;
				try {
					i = Integer.parseInt(last);
					if (i > max) {
						max = i;
					}
				} catch (NumberFormatException e) {
				}
				
			}
		}
		File f = FileOperations.safeSubdir(parrent, nameStart + prefix + "_" + (max + 1));
		System.out.println("creating " + f);
		return f;
	}
	
	public File getParameters() {
		System.out.println(getHomePath() + " **");
		System.out.println(getHomePath().resolve("parameters.txt"));
		File f = getHomePath().resolve("parameters.txt").toFile();
		System.out.println("resolve " + f.getAbsolutePath());
		return getHomePath().resolve("parameters.txt").toFile();
	}
	
	public void setStructures(String structuresDirName) {
		structures = getTask().toPath().resolve(structuresDirName).toFile();
	}
	
	public File getStructures() {
		return structures;
	}
	
	public File getSummaryTable() {
		return FileOperations.safeSubfile(getJob(), "summary.txt");
	}
	
	public File getPyFile() {
		return FileOperations.safeSub(getTask(), "alignments.py");
	}
	
	public File getBiwordVisualization() {
		return FileOperations.safeSubfile(getHome(), "biword_visualization");
	}
	
	public File getWordConnections(StructureSource source) {
		if (source.toString().endsWith(".pdb")) {
			return FileOperations.safeSub(getBiwordVisualization(), "bw_" + source.toString());
		} else {
			return FileOperations.safeSub(getBiwordVisualization(), "bw_" + source + ".pdb");
		}
	}
	
	public File getResultsFile() {
		return FileOperations.safeSub(getTask(), "results.txt");
	}
	
	public File getTableFile() {
		return FileOperations.safeSub(getTask(), "table.csv");
	}
	
	public File getCathS20() {
		return FileOperations.safeSub(getTask(), "cath-dataset-nonredundant-S20.list.txt");
	}
	
	public File getCathDomainBoundaries() {
		return FileOperations.safeSub(getHome(), "cath-domain-boundaries-v4_2_0.txt");
	}
	
	public File getCathNames() {
		return FileOperations.safeSub(getHome(), "cath-names-v4_2_0.txt");
	}
	
	public File getCathDomainList() {
		return FileOperations.safeSub(getHome(), "cath-domain-list-v4_2_0.txt");
	}
	
	public File getPdbClusters50() {
		return FileOperations.safeSubfile(getTask(), "bc-50.out.txt");
	}

	/**
	 * @deprecated
	 */
	public File getWordDataset() {
		return FileOperations.safeSubfile(getTask(), "word_dataset");
	}
	
	public File getWordDatabase() {
		return FileOperations.safeSubfile(getTask(), "word_database");
	}
	
	private File getStructureSetsDir() {
		return FileOperations.safeSubdir(getHome(), "indexes");
	}
	
	public File getIndex(String id) {
		return FileOperations.safeSubfile(getStructureSetsDir(), id);
	}
	
	public File getBiwordPair() {
		return FileOperations.safeSubfile(getTask(), "biword_pair.pdb");
	}
	
	public File getWordDatasetShuffled() {
		return FileOperations.safeSubfile(getTask(), "word_dataset_shuffled");
	}
	
	public File getRealVsVector() {
		File f = null;
		int i = 0;
		while (f == null || f.exists()) {
			f = FileOperations.safeSubfile(getTask(), "real_vector_" + i + ".csv");
			i++;
		}
		return f;
	}
	
	private Path getMmtf() {
		Path p = getHomePath().resolve("mmtf");
		createDirs(p);
		return p;
	}
	
	public File getBiwordHits(int structureId) {
		Path p = getBiwordHitsDir();
		createDirs(p);
		return p.resolve(Integer.toString(structureId)).toFile();
	}
	
	public Path getBiwordHitsDir() {
		Path p = getTask().toPath().resolve("biword_hits");
		return p;
	}

	/*
	 * Use specified subdirectory in application's home directory.
	 */
 /*public File getBiwordedStructure(String structureSetId, int structureId, String externalSourceRelativePath) {
		Path dir = getBiwordsDir(externalSourceRelativePath);
		File file = getBiwordsFile(dir, structureId);
		return file;
	}*/

 /*
	 * Use default directory within task, when starting from scratch and structure are not preprocessed.
	 */
	public File getBiwordedStructure(String structureSetId, int structureId) {
		Path dir = getBiwordsDir(structureSetId);
		createDirs(dir);
		return getBiwordsFile(dir, structureId);
	}
	
	private File getBiwordsFile(Path dir, int structureId) {
		return dir.resolve(Integer.toString(structureId)).toFile();
	}
	
	public Path getBiwordsDir(String structureSetId) {
		Path p = getHome().toPath().resolve("biwords");
		createDirs(p);
		return p.resolve(structureSetId);
	}

	/*public Path getBiwordsDir(String externalSourceRelativePath) {
		Path dir = getHomePath().resolve(externalSourceRelativePath);
		return dir;
	}*/
	public Path getMmtf(String code) {
		return getMmtf().resolve(code + ".mmtf.gz");
	}
	
	public Path getPdb() {
		Path p = getHomePath().resolve("pdb");
		createDirs(p);
		return p;
	}
	
	public Path getPdb(String code) {
		return getPdb().resolve(code + ".pdb.gz");
	}
	
	public File getOutputStrucureFile(String filename) {
		return getAlignedPdbsDir().toPath().resolve(filename).toFile();
	}
	
	public File getPdbFasta() {
		return FileOperations.safeSub(getHome(), "pdb_seqres.txt");
	}
	
	public Path getPairs() {
		return FileOperations.safeSub(getHome(), "pairs.csv").toPath();
	}
	
	public File getTopologyIndependentPairs() {
		return FileOperations.safeSub(getHome(), "89_similar_structure_diff_topo.txt");
	}
	
	public File getHomstradPairs() {
		return FileOperations.safeSub(getHome(), "9537_pair_wise_HOMSTRAD.txt");
	}
	
	public File getFailedPairs() {
		return FileOperations.safeSub(getHome(), "fails.txt");
	}
	
	public File get89Pairs() {
		return FileOperations.safeSub(getHome(), "89_similar_structure_diff_topo.txt");
	}
	
	public File getCustomPairs() {
		return FileOperations.safeSub(getHome(), "pairs.txt");
	}
	
	public File getMalidupPairs() {
		return getHomePath().resolve("benchmarks").resolve("MALIDUP-ns").resolve("pdb").toFile();
	}
	
	public File getMalisamPairs() {
		return getHomePath().resolve("benchmarks").resolve("MALISAM-ns").resolve("pdb").toFile();
	}
	
	public List<String> loadBatch() {
		List<String> batch = new ArrayList<>();
		File f = FileOperations.safeSub(getTask(), "batch.txt");
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line;
			while ((line = br.readLine()) != null) {
				batch.add(line.trim().toUpperCase());
			}
			br.close();
		} catch (IOException ex) {
			FlexibleLogger.error("Failed to load batch file " + f.getPath(), ex);
		}
		return batch;
	}
	
	public File getPdbEntryTypes() {
		return FileOperations.safeSubfile(getHome(), "pdb_entry_type.txt");
	}
	
	public File getCustomTargets() {
		return FileOperations.safeSubfile(getHome(), "target_codes.txt");
	}
	
	public File getQueryCodes() {
		return FileOperations.safeSubfile(getHome(), "query_codes.txt");
	}
	
	public File getTemp() {
		return FileOperations.safeSub(getTask(), "temp");
	}

	/*
	public void setPdbCode(String pc) {
		this.pdbCode = pc;
	}

	public File getClustersTxt() {
		String name = pdbCode + "clusters.txt";
		return FileOperations.safeSub(getTask(), name);
	}

	public File getClustersPy() {
		String name = pdbCode + "clusters.py";
		return FileOperations.safeSub(getTask(), name);
	}

	public File getClusterPdb(int i) {
		String name = pdbCode + "cluster_" + i + ".pdb";
		return FileOperations.safeSub(getTask(), name);
	}

	public File getClustersPng() {
		String name = pdbCode + "clusters.png";
		return FileOperations.safeSub(getTask(), name);
	}*/
	public File getCluster() {
		return FileOperations.safeSubdir(getTask(), "fragment_clusters");
	}
	
	public File getCluster(int id) {
		return FileOperations.safeSubfile(getCluster(), id + ".pdb");
	}
	
	public File getAlignmentResults() {
		return FileOperations.safeSub(getTask(), "alignment_results");
	}
	
	public File getVisDir() {
		return FileOperations.safeSub(getTask(), "vis");
	}
	
	public File getVis(String id) {
		return FileOperations.safeSub(getVisDir(), id + ".pdb");
	}
	
	public File getAlignedPdbsDir() {
		return FileOperations.safeSub(getVisDir(), "aligned_pdbs");
	}
	
	public File getFatcatResults() {
		return FileOperations.safeSubfile(getAlignmentResults(), "fatcat.results");
	}
	
	public File getFragmentsResults() {
		return FileOperations.safeSubfile(getAlignmentResults(), "fragments.results");
	}
	
	public File getAlignmentObjects() {
		return FileOperations.safeSubfile(getAlignmentResults(), "alignemnt.cryo");
	}
	
	public File getAlignmentCsv() {
		return FileOperations.safeSubfile(getAlignmentResults(), "alignment.csv");
	}
	
	public File getAlignmentCsvBackup() {
		return FileOperations.safeSubfile(getAlignmentResults(), "alignment_backup.csv");
	}
	
	private Path getClickInputDir(Pair<String> pair) throws IOException {
		Path dir = getTask().toPath().resolve("click_input").resolve(pair.x + "-" + pair.y);
		if (!Files.exists(dir)) {
			Files.createDirectories(dir);
		}
		return dir;
	}
	
	public Path getClickInput(Pair<String> pair, String id) throws IOException {
		return getClickInputDir(pair).resolve(id + ".pdb");
	}
	
	public Path getClickOutputDir() {
		Path dir = getTask().toPath().resolve("click_input");
		return dir;
	}
	
	public Path getClickOutput(Pair<String> pair, String a, String b) throws IOException {
		return getClickOutputDir().resolve(pair.x + "-" + pair.y).resolve(a + "-" + b + ".1.pdb");
	}
	
}
