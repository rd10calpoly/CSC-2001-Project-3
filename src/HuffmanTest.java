import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Starter test suite for Project 3 (Huffman Encoding/Decoding).
 * Write more tests!
 * These tests assume the test files are in the same directory as HuffmanTest.java
 */
public class HuffmanTest {

    @Test
    public void testCntFreq() throws IOException {
        int[] freqList = Huffman.cntFreq("CSC-2001-Project-3/file2.txt");
        int[] ansList = {2, 4, 8, 16, 0, 2, 0}; // counts for ascii 97..103
        for (int i = 0; i < ansList.length; i++) {
            assertEquals(ansList[i], freqList[97 + i], "mismatch at ascii " + (97 + i));
        }
    }

    @Test
    public void testComesBefore() {
        HuffmanNode d = new HuffmanNode('d', 5);
        HuffmanNode k = new HuffmanNode('k', 5);
        assertTrue(HuffmanNode.comesBefore(d, k));
        assertFalse(HuffmanNode.comesBefore(k, d));
        HuffmanNode l1 = new HuffmanNode('l', 10);
        HuffmanNode l2 = new HuffmanNode('l', 10);
        HuffmanNode h1 = new HuffmanNode('h', 10);
        HuffmanNode h2 = new HuffmanNode('h', 8);
        assertFalse(HuffmanNode.comesBefore(l1, l2));
        assertFalse(HuffmanNode.comesBefore(l2, l1));
        assertFalse(HuffmanNode.comesBefore(h1, h2));
        assertTrue(HuffmanNode.comesBefore(h2, h1));
    }

    @Test
    public void testCreateHeader() throws IOException {
        int[] freqList = Huffman.cntFreq("CSC-2001-Project-3/file2.txt");
        assertEquals("97 2 98 4 99 8 100 16 102 2", Huffman.createHeader(freqList));
    }

    @Test
    public void test01Textfile() throws IOException {
        Huffman.huffmanEncode("CSC-2001-Project-3/file1.txt", "file1_out.txt");
        assertTrue(compareFiles("file1_out.txt", "CSC-2001-Project-3/file1_soln.txt"));
    }

    @Test
    public void isLeaf() {
        HuffmanNode n = new HuffmanNode(100, 1, null, null);
        assertTrue(n.isLeaf());
        HuffmanNode root = new HuffmanNode(100, 2, null, n);
        assertFalse(root.isLeaf());
    }

    @Test
    public void combine() {
        HuffmanNode h1 = new HuffmanNode('h', 10);
        HuffmanNode h2 = new HuffmanNode('h', 8);
        HuffmanNode h3 = HuffmanNode.combine(h1, h2);
        assertEquals(18, h3.freq());
        assertEquals('h', h3.charAscii());


        HuffmanNode s = new HuffmanNode('s', 7);
        HuffmanNode l = new HuffmanNode('l', 97);
        HuffmanNode combined = HuffmanNode.combine(s, l);
        assertEquals(104, combined.freq());
        assertEquals('l', combined.charAscii());

        HuffmanNode semicolon = new HuffmanNode(';', 1);
        HuffmanNode exclaim = new HuffmanNode('!', 1);
        HuffmanNode combined1 = HuffmanNode.combine(semicolon, exclaim);
        assertEquals(2, combined1.freq());
        assertEquals('!', combined1.charAscii());
    }

    @Test
    public void compareTo() {
        HuffmanNode h1 = new HuffmanNode('h', 10);
        HuffmanNode h2 = new HuffmanNode('h', 8);
        HuffmanNode h3 = new HuffmanNode('h', 10);
        assertEquals(1, h1.compareTo(h2));
        assertEquals(0, h1.compareTo(h3));
        assertEquals(-1, h2.compareTo(h1));
    }

    @Test
    public void removeMin() {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        assertNull(Huffman.removeMin(pq));


        HuffmanNode huffmanNode = new HuffmanNode(35, 2);
        HuffmanNode huffmanNode1 = new HuffmanNode(69, 3);
        HuffmanNode combined = HuffmanNode.combine(huffmanNode, huffmanNode1);
        pq.add(huffmanNode);
        pq.add(huffmanNode1);
        pq.add(combined);
        assertEquals(huffmanNode, Huffman.removeMin(pq));
        assertEquals(huffmanNode1, Huffman.removeMin(pq));
        assertEquals(combined, Huffman.removeMin(pq));


        ArrayList<HuffmanNode> al = new ArrayList<>();
        HuffmanNode huffmanNode2 = new HuffmanNode(98, 2);
        HuffmanNode huffmanNode3 = new HuffmanNode(77, 3);
        HuffmanNode huffmanNode4 = new HuffmanNode(100, 3);
        HuffmanNode huffmanNode5 = new HuffmanNode(98, 1);
        HuffmanNode combined1 = HuffmanNode.combine(huffmanNode2, huffmanNode3);
        HuffmanNode combined2 = HuffmanNode.combine(combined1, huffmanNode4);
        al.add(huffmanNode2);
        al.add(huffmanNode3);
        al.add(huffmanNode4);
        al.add(huffmanNode5);
        al.add(combined1);
        al.add(combined2);
        assertEquals(huffmanNode5, Huffman.removeMin(al));
        assertEquals(huffmanNode2, Huffman.removeMin(al));
        assertEquals(huffmanNode3, Huffman.removeMin(al));
        assertEquals(huffmanNode4, Huffman.removeMin(al));
        assertEquals(combined1, Huffman.removeMin(al));
        assertEquals(combined2, Huffman.removeMin(al));
    }

    @Test
    public void createHuffTree() {
        int[] freq1 = new int[256];
        freq1[68] = 21;
        freq1[69] = 33;
        Huffman.createHuffTree(freq1);
        assertEquals(new HuffmanNode(68, 54, new HuffmanNode(68, 21, null, null), new HuffmanNode(69, 33, null, null)), Huffman.createHuffTree(freq1));


        String header2 = "1 2 3 4";
        int[] parsed2 = Huffman.parseHeader(header2);
        int[] freq2 = new int[256];
        for (int i = 1; i <= 3; i += 2) {
            freq2[i] = i + 1;
        }
        assertEquals(new HuffmanNode(1, 6, new HuffmanNode(1, 2, null, null), new HuffmanNode(3, 4, null, null)), Huffman.createHuffTree(freq2));

        int[] freq3 = new int[256];
        assertNull(Huffman.createHuffTree(freq3));
    }

    @Test
    public void createCode() {
        String[] nullCheck = new String[256];
        Arrays.fill(nullCheck, "");
        assertArrayEquals(nullCheck, Huffman.createCode(null));


        HuffmanNode a = new HuffmanNode(65, 5, null, null);
        HuffmanNode b = new HuffmanNode(66, 3, null, null);
        HuffmanNode c = new HuffmanNode(67, 2, null, null);
        HuffmanNode right = HuffmanNode.combine(a, b);
        HuffmanNode root = HuffmanNode.combine(right, c);
        String[] codes = Huffman.createCode(root);
        assertEquals("11", codes[65]);
        assertEquals("10", codes[66]);
        assertEquals("0", codes[67]);
    }

    @Test
    public void huffmanEncode() throws IOException {
        Huffman.huffmanEncode("CSC-2001-Project-3/file1.txt", "file1_out.txt");
        assertTrue(compareFiles("file1_out.txt", "CSC-2001-Project-3/file1_soln.txt"));
        Huffman.huffmanEncode("CSC-2001-Project-3/file2.txt", "file2_out.txt");
        assertTrue(compareFiles("file2_out.txt", "CSC-2001-Project-3/file2_soln.txt"));
        Huffman.huffmanEncode("CSC-2001-Project-3/file3.txt", "file3_out.txt");
        assertTrue(compareFiles("file3_out.txt", "CSC-2001-Project-3/file3_soln.txt"));
        Huffman.huffmanEncode("CSC-2001-Project-3/file4.txt", "file4_out.txt");
        assertTrue(compareFiles("file4_out.txt", "CSC-2001-Project-3/file4_soln.txt"));
        Huffman.huffmanEncode("CSC-2001-Project-3/file5.txt", "file5_out.txt");
        assertTrue(compareFiles("file5_out.txt", "CSC-2001-Project-3/file5_soln.txt"));
        Huffman.huffmanEncode("CSC-2001-Project-3/file6.txt", "empty_out.txt");
        Huffman.huffmanEncode("CSC-2001-Project-3/multiplelines.txt", "multiplelines_out.txt");
        assertTrue(compareFiles("multiplelines_out.txt", "CSC-2001-Project-3/multiplelines_soln.txt"));
        assertTrue(compareFiles("empty_out.txt", "CSC-2001-Project-3/file6_soln.txt"));
        assertFalse(compareFiles("empty_out.txt", "CSC-2001-Project-3/file5_soln.txt"));
        assertFalse(compareFiles("CSC-2001-Project-3/file5_soln.txt", "empty_out.txt"));
        assertFalse(compareFiles("CSC-2001-Project-3/file5_soln.txt", "file3_out.txt"));

        Huffman.huffmanEncode("CSC-2001-Project-3/char.txt", "char_out.txt");
        assertTrue(compareFiles("char_out.txt", "CSC-2001-Project-3/char_soln.txt"));

    }

    @Test
    public void parseHeader() {
        String header1 = "68 1 69 3 70 6 78 5 81 11 88 12 91 2 96 3";
        int[] parsed1 = Huffman.parseHeader(header1);
        int[] freq1 = new int[256];
        freq1[68] = 1;
        freq1[69] = 3;
        freq1[70] = 6;
        freq1[78] = 5;
        freq1[81] = 11;
        freq1[88] = 12;
        freq1[91] = 2;
        freq1[96] = 3;
        assertArrayEquals(parsed1, freq1);

        String header2 = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16";
        int[] parsed2 = Huffman.parseHeader(header2);
        int[] freq2 = new int[256];
        for (int i = 1; i <= 15; i += 2) {
            freq2[i] = i + 1;
        }
        assertArrayEquals(parsed2, freq2);
    }

    @Test
    public void huffmanDecode() throws IOException {
        Huffman.huffmanDecode("CSC-2001-Project-3/file1_soln.txt", "file1_decoded.txt");
        assertTrue(compareFiles("file1_decoded.txt", "CSC-2001-Project-3/file1.txt"));
        Huffman.huffmanDecode("CSC-2001-Project-3/file2_soln.txt", "file2_decoded.txt");
        assertTrue(compareFiles("file2_decoded.txt", "CSC-2001-Project-3/file2.txt"));
        Huffman.huffmanDecode("CSC-2001-Project-3/file3_soln.txt", "file3_decoded.txt");
        assertTrue(compareFiles("file3_decoded.txt", "CSC-2001-Project-3/file3.txt"));
        Huffman.huffmanDecode("CSC-2001-Project-3/file4_soln.txt", "file4_decoded.txt");
        assertTrue(compareFiles("file4_decoded.txt", "CSC-2001-Project-3/file4.txt"));
        Huffman.huffmanDecode("CSC-2001-Project-3/file5_soln.txt", "file5_decoded.txt");
        assertTrue(compareFiles("file5_decoded.txt", "CSC-2001-Project-3/file5.txt"));
        Huffman.huffmanDecode("CSC-2001-Project-3/file6_soln.txt", "file6_decoded.txt");
        assertTrue(compareFiles("file6_decoded.txt", "CSC-2001-Project-3/file6.txt"));
        Huffman.huffmanDecode("CSC-2001-Project-3/multiplelines_soln.txt", "multiplelines_decoded.txt");
        assertTrue(compareFiles("multiplelines_decoded.txt", "CSC-2001-Project-3/multiplelines.txt"));
        Huffman.huffmanDecode("CSC-2001-Project-3/char_soln.txt", "char_decoded.txt");
        assertTrue(compareFiles("char_decoded.txt", "CSC-2001-Project-3/char.txt"));
    }
    /**
     * Compares two files line by line, trimming trailing CR/LF so the
     * comparison works the same on Windows and Unix-style line endings.
     * @param file1: String
     * @param file2: String
     * @return boolean
     */
    private static boolean compareFiles(String file1, String file2) throws IOException {
        try (BufferedReader r1 = new BufferedReader(new FileReader(file1));
             BufferedReader r2 = new BufferedReader(new FileReader(file2))) {
            String line1, line2;
            while (true) {
                line1 = r1.readLine();
                line2 = r2.readLine();
                if (line1 == null && line2 == null) {
                    return true;
                }
                if (line1 == null || line2 == null) {
                    return false;
                }
                if (!line1.trim().equals(line2.trim())) {
                    return false;
                }
            }
        }
    }
}
