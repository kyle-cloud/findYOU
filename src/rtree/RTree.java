package rtree;

import java.util.ArrayList;
import java.util.List;

import rtree.Constants;

/**
 * @ClassName RTree
 * @Description
 */
public class RTree {
    private RTNode root; // ���ڵ�
    private int tree_type; // ������
    private int nodeCapacity = -1; // �������
    private float fillFactor = -1; // ���������� �����ڼ���ÿ�������С��Ŀ����
    private int dimension; // ά��

    public RTree(int capacity, float fillFactor, int type, int dimension) {
        this.fillFactor = fillFactor;
        tree_type = type;
        nodeCapacity = capacity;
        this.dimension = dimension;
        root = new RTDataNode(this, Constants.NULL); // ���ڵ�ĸ��ڵ�ΪNULL
    }

    /**
     * @return RTree��ά��
     */
    public int getDimension() {
        return dimension;
    }

    /** ���ø��ڵ� */
    public void setRoot(RTNode root) {
        this.root = root;
    }

    /**
     * @return �������
     */
    public float getFillFactor() {
        return fillFactor;
    }

    /**
     * @return ���ؽ������
     */
    public int getNodeCapacity() {
        return nodeCapacity;
    }

    /**
     * @return ������������
     */
    public int getTreeType() {
        return tree_type;
    }

    /**
     * --> ��Rtree�в���Rectangle 1�����ҵ����ʵ�Ҷ�ڵ� 2�������Ҷ�ڵ��в���
     * 
     * @param rectangle
     */
    public boolean insert(Rectangle rectangle) {
        if (rectangle == null)
            throw new IllegalArgumentException("Rectangle cannot be null.");

        if (rectangle.getHigh().getDimension() != getDimension()) // ����ά��������ά�Ȳ�һ��
        {
            throw new IllegalArgumentException("Rectangle dimension different than RTree dimension.");
        }

        RTDataNode leaf = root.chooseLeaf(rectangle);

        return leaf.insert(rectangle);
    }

    /**
     * ��R����ɾ��Rectangle
     * <p>
     * 1��Ѱ�Ұ�����¼�Ľ��--�����㷨findLeaf()����λ�����˼�¼��Ҷ�ӽ��L�����û���ҵ����㷨��ֹ��<br>
     * 2��ɾ����¼--���ҵ���Ҷ�ӽ��L�еĴ˼�¼ɾ��<br>
     * 3�������㷨condenseTree<br>
     * 
     * @param rectangle
     * @return
     */
    public int delete(Rectangle rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("Rectangle cannot be null.");
        }

        if (rectangle.getHigh().getDimension() != getDimension()) {
            throw new IllegalArgumentException("Rectangle dimension different than RTree dimension.");
        }

        RTDataNode leaf = root.findLeaf(rectangle);	

        if (leaf != null) {
            return leaf.delete(rectangle);
        }

        return -1;
    }

    /**
     * �Ӹ����Ľ��root��ʼ�������еĽ��
     * 
     * @param node
     * @return ���б����Ľ�㼯��
     */
    public List<RTNode> traversePostOrder(RTNode root) {
        if (root == null)
            throw new IllegalArgumentException("Node cannot be null.");

        List<RTNode> list = new ArrayList<RTNode>();
        list.add(root);

        if (!root.isLeaf()) {
            for (int i = 0; i < root.usedSpace; i++) {
                List<RTNode> a = traversePostOrder(((RTDirNode) root).getChild(i));
                for (int j = 0; j < a.size(); j++) {
                    list.add(a.get(j));
                }
            }
        }

        return list;
    }

    public static void main(String[] args) throws Exception {
        // ���������4��������ӣ�0.4�������ͣ���ά
        RTree tree = new RTree(4, 0.4f, Constants.RTREE_QUADRATIC, 2);
        // ÿһ�е��ĸ������������㣨һ�����Σ�
        float[] f = { 5, 30, 25, 35, 15, 38, 23, 50, 10, 23, 30, 28, 13, 10, 18, 15, 23, 10, 28, 20, 28, 30, 33, 40, 38,
                13, 43, 30, 35, 37, 40, 43, 45, 8, 50, 50, 23, 55, 28, 70, 10, 65, 15, 70, 10, 58, 20, 63, };

        // ������
        for (int i = 0; i < f.length;) {
            Point p1 = new Point(new float[] { f[i++], f[i++] });
            Point p2 = new Point(new float[] { f[i++], f[i++] });
            final Rectangle rectangle = new Rectangle(p1, p2);
            tree.insert(rectangle);

            Rectangle[] rectangles = tree.root.datas;
            System.out.println("level:" + tree.root.level);
            for (int j = 0; j < rectangles.length; j++)
                System.out.println(rectangles[j]);
        }
        System.out.println("---------------------------------");
        System.out.println("Insert finished.");

        // ɾ�����
        System.out.println("---------------------------------");
        System.out.println("Begin delete.");

        for (int i = 0; i < f.length;) {
            Point p1 = new Point(new float[] { f[i++], f[i++] });
            Point p2 = new Point(new float[] { f[i++], f[i++] });
            final Rectangle rectangle = new Rectangle(p1, p2);
            tree.delete(rectangle);

            Rectangle[] rectangles = tree.root.datas;
            System.out.println(tree.root.level);
            for (int j = 0; j < rectangles.length; j++)
                System.out.println(rectangles[j]);
        }

        System.out.println("---------------------------------");
        System.out.println("Delete finished.");

        Rectangle[] rectangles = tree.root.datas;
        for (int i = 0; i < rectangles.length; i++)
            System.out.println(rectangles[i]);

    }

}