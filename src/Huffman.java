import java.io.*;
import java.util.*;

/**
 * Project 3: Huffman Encoding / Decoding.
 *
 * Implement the methods below to compress and decompress text files using
 * Huffman coding. See the assignment write-up for the full specification.
 *
 *  - Treat characters as 8-bit values in the range [0, 255].
 *  - When writing output files, write the newline character explicitly as
 *    '\n' (do not rely on System.lineSeparator() / println()), so your
 *    output is identical across Windows/Mac/Linux. A BufferedWriter over
 *    a FileWriter, writing "\n" yourself, works well for this.
 *  - Encoded files store the code as literal '0'/'1' characters (one byte
 *    each), not as packed bits, so the "encoded" file will actually be
 *    larger than the original -- that's expected for this assignment.
 *  - createHuffTree() is built on top of addNode()/removeMin(), which
 *    operate generically over a java.util.Collection<HuffmanNode>.
 *  - HuffmanNode is a record, so its components are read with
 *    node.charAscii(), node.freq(), node.left(), and node.right() -- no
 *    "get" prefix.
 */
public class Huffman {

    /** Number of possible 8-bit character values. */
    public static final int NUM_CHARS = 256;

    /**
     * Opens the text file named filename and counts the frequency of
     * occurrence of every character in it.
     * Returns an int array of size NUM_CHARS, indexed by ASCII value, with
     * the count of each character (0 for characters that do not occur).
     * @param filename: String
     * @return int[]
     */
    public static int[] cntFreq(String filename) throws IOException {
        int[] freq = new int[NUM_CHARS];
        // TODO: implement
        File f = new File(filename);
        FileReader reader = new FileReader(f);
        int c = reader.read();
        while (c != -1) {
            freq[c]++;
            c = reader.read();
        }
        reader.close();
        return freq;
    }

    /**
     * Adds node to collection, preserving whatever ordering your chosen
     * implementation of collection relies on to make the minimum element
     * easy to find.
     *
     * You choose what kind of Collection to pass in when you call this
     * from createHuffTree(). For example, an ArrayList kept sorted by
     * HuffmanNode.comesBefore() as you insert (an "ordered list"), or a
     * PriorityQueue. How you implement this method depends on which you pick.
     * @param collection: Collection<HuffmanNode>
     * @param node: HuffmanNode
     * (1) collection: [0, 1, 2, 3, 4] -> add(5) -> collection: [0, 1, 2, 3, 4, 5]
     */
    public static void addNode(Collection<HuffmanNode> collection, HuffmanNode node) {
        // TODO: implement
        collection.add(node);
    }

    /**
     * Removes and returns the smallest HuffmanNode in collection, using
     * HuffmanNode.comesBefore() (or compareTo(), which relies on it) to
     * determine "smallest". Returns null if collection is empty.txt.
     *
     * This must work for ANY Collection passed in -- not just the one you
     * use inside createHuffTree(). For example, if you back createHuffTree()
     * with a PriorityQueue, poll() it; for any other collection, find the
     * minimum node and remove it.
     * @param collection: Collection<HuffmanNode>
     * @return HuffmanNode
     * (1) let collection be ArrayList
     * (1) collection: {3, 4, 5, 10, 13} -> removeMin(collection) -> {4, 5, 10, 13}
     */
    public static HuffmanNode removeMin(Collection<HuffmanNode> collection) {
        // TODO: implement
        if (collection.isEmpty()) {
            return null;
        }

        if (collection instanceof PriorityQueue<HuffmanNode>) {
            PriorityQueue<HuffmanNode> pq = (PriorityQueue<HuffmanNode>) collection;
            return pq.poll();
        } else {
            HuffmanNode minimum = null;
            for (HuffmanNode n : collection) {
                if (minimum == null || HuffmanNode.comesBefore(n, minimum)) {
                    minimum = n;
                }
            }
            collection.remove(minimum);
            return minimum;
        }
    }

    /**
     * Builds a Huffman tree from an array of character frequencies (as
     * produced by cntFreq()) and returns the root HuffmanNode of the tree.
     *
     * Use addNode()/removeMin() above to manage the collection of
     * not-yet-combined nodes as you build the tree -- pick whichever
     * concrete Collection type you want to back them with (e.g. a sorted
     * ArrayList, or a PriorityQueue). The rest of this method, and the
     * resulting tree, should not depend on which one you chose.
     *
     * Special cases:
     *  - If freq contains no non-zero counts (empty.txt input file), return null
     *  - If freq contains exactly one non-zero count (input file has only
     *    one unique character), return a single leaf HuffmanNode for that
     *    character (no combine() call needed).
     * @param freq: int[]
     * @return HuffmanNode
     * (1) {4, 3, 3} -> root(10) -> left(4) -> right(6) -> right, left(3) -> right, right(3)
     */
    public static HuffmanNode createHuffTree(int[] freq) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (int i = 0; i < NUM_CHARS; i++) {
            if (freq[i] > 0) {
                HuffmanNode h = new HuffmanNode((char) i, freq[i], null, null);
                addNode(pq, h);
            }
        }
        if (pq.isEmpty()) {
            return null;
        }
        while (pq.size() > 1) {
            HuffmanNode left = removeMin(pq);
            HuffmanNode right = removeMin(pq);
            addNode(pq, HuffmanNode.combine(left, right));
        }
        return removeMin(pq);
    }

    /**
     * Traverses the Huffman tree rooted at root and builds the Huffman
     * code for every character that appears in it. Going left appends
     * '0' to the code, going right appends '1'.
     *
     * Returns a String array of size NUM_CHARS, indexed by ASCII value.
     * Characters that do not appear in the tree have an empty.txt string ""
     * at their index. The same is true of the lone character in a
     * single-node tree (the only-one-unique-character case) - it has no
     * left/right branch to build a code out of; huffmanEncode/huffmanDecode
     * handle that case entirely through the header.
     * @param root: HuffmanNode
     * @return String[]
     *  (1) 4 -> 2 (left) -> 2 (right) -> 'G' (left, left) -> 'H' (left, right) -> 'J' (right, left) -> 'M' (right, right)
     *  'G' -> 00; 'H' -> 01; 'J' -> 10; 'K' -> 11
     */
    public static String[] createCode(HuffmanNode root) {
        String[] codes = new String[NUM_CHARS];
        Arrays.fill(codes, "");
        // TODO: implement
        if (root == null) {
            return codes;
        }
        ArrayList<HuffmanNode> nodes = new ArrayList<>();
        ArrayList<String> codedPaths = new ArrayList<>();
        nodes.add(root);
        codedPaths.add("");
        while (!nodes.isEmpty()) {
            HuffmanNode currentNode = nodes.remove(0);
            String currentPath = codedPaths.remove(0);

            if (currentNode.isLeaf()) {
                codes[currentNode.charAscii()] = currentPath;
            } else {
                HuffmanNode left = currentNode.left();
                HuffmanNode right = currentNode.right();
                if (left != null) {
                    nodes.add(left);
                    codedPaths.add(currentPath + "0");
                }
                if (right != null) {
                    nodes.add(right);
                    codedPaths.add(currentPath + "1");
                }
            }
        }
        return codes;
    }

    /**
     * Given a frequency array (as produced by cntFreq()), returns a
     * header string listing the ASCII values and their associated
     * frequencies, separated by single spaces, in ascending order of
     * ASCII value. Only characters with a non-zero count are included.
     *
     * Example: for the text "aaabbbbcc" this returns "97 3 98 4 99 2".
     * @param freq: int[]
     * @return String
     * (1) createHeader -> "randy" -> returns "97 1 100 1 110 1 114 1 121 1"
     */
    public static String createHeader(int[] freq) {
        // TODO: implement
        String result = "";
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] > 0) {
                result += i + " " + freq[i] + " ";
            }
        }
        if (result.length() == 0) {
            return "";
        } else {
            return result.substring(0, result.length() - 1);
        }
    }

    /**
     * Reads the input text file inFile and writes to outFile:
     *   1. A header line (see createHeader), followed by a newline
     *   2. The Huffman-encoded contents of inFile, using the codes from
     *      createCode() -- UNLESS inFile has only one unique character,
     *      see below.
     * If outFile already exists, its old contents are overwritten.
     *
     * Special cases:
     *  - Empty input file: outFile should end up empty.txt too
     *  - Input file with only one unique character: should contain only the
     *    header line (e.g. "97 5") and nothing else - no second line of code bits.
     * @param inFile: String
     * @param outFile: String
     * (1) inFile -> "babab", HuffmanTree -> root(2) -> left(a) -> right(b), outFile -> "10101"
     */
    public static void huffmanEncode(String inFile, String outFile) throws IOException {
        // TODO: implement
        int[] countFreq = cntFreq(inFile);
        String header = createHeader(countFreq);
        FileWriter writer = new FileWriter(outFile);
        if (header.isEmpty()) {
            writer.close();
            return;
        }
        writer.write(header + "\n");
        HuffmanNode tree = createHuffTree(countFreq);
        if (tree.isLeaf()) {
            writer.close();
            return;
        }
        String[] treeCode = createCode(tree);
        FileReader reader = new FileReader(inFile);
        int r = reader.read();
        while (r != -1) {
            writer.write(treeCode[r]);
            r = reader.read();
        }
        reader.close();
        writer.close();
    }

    /**
     * Parses a header string (in the format produced by createHeader) and
     * returns the corresponding frequency array of size NUM_CHARS, indexed
     * by ASCII value, i.e. the same format returned by cntFreq().
     *
     * Example: for the header "97 3 98 4 99 2", freq[97, 98, 99] == {3, 4, 2}.
     * @param headerString: String
     * @return int[]
     * (1) "65 2 98 11 100 5" -> freq[65, 98, 100] -> {2, 11, 5}
     */
    public static int[] parseHeader(String headerString) {
        int[] freq = new int[NUM_CHARS];
        String[] valuesInHeaders = headerString.split(" ");
        for (int i = 0; i < valuesInHeaders.length; i += 2) {
            int character = Integer.parseInt(valuesInHeaders[i]);
            int frequencyNumber = Integer.parseInt(valuesInHeaders[i + 1]);
            freq[character] = frequencyNumber;
        }
        // TODO: implement
        return freq;
    }

    /**
     * Reads an encoded file (in the format written by huffmanEncode) and
     * writes the decoded text to decodeFile.
     *
     * Recreate the frequency array from the header with parseHeader(),
     * rebuild the Huffman tree with createHuffTree(), then walk the tree
     * for each bit of the second line: '0' -> left child, '1' -> right
     * child. When a leaf is reached, emit its character and restart at
     * the root.
     *
     * Special case: if the recreated tree is a single leaf (the
     * only-one-unique-character case), there is no second line to read
     * The header's count alone tells you how many times that character occurred,
     * so write it out that many times directly.
     * @param encodedFile: String
     * @param decodeFile: String
     *  (1) encodedFile -> "10101", HuffmanTree -> root(2) -> left(a) -> right(b), decodedFile("babab")
     */
    public static void huffmanDecode(String encodedFile, String decodeFile) throws IOException {
        // TODO: implement
        File file = new File(encodedFile);
        Scanner scan = new Scanner(file);
        FileWriter writer = new FileWriter(decodeFile);
        if (!scan.hasNextLine()) {
            scan.close();
            writer.close();
            return;
        }
        String firstLineHeader = scan.nextLine();
        int[] frequencies = parseHeader(firstLineHeader);
        HuffmanNode tree = createHuffTree(frequencies);
        if (tree.isLeaf()) {
            for (int i = 0; i < tree.freq(); i++) {
                writer.write(tree.charAscii());
            }
            writer.close();
            return;
        }
        String binaryCode = scan.nextLine();
        HuffmanNode curr = tree;
        for (int i = 0; i < binaryCode.length(); i++) {
            char digit = binaryCode.charAt(i);
            if (digit == '0') {
                curr = curr.left();
            } else if (digit == '1') {
                curr = curr.right();
            }
            if (curr.isLeaf()) {
                writer.write(curr.charAscii());
                curr = tree;
            }
        }
        scan.close();
        writer.close();
    }
}
