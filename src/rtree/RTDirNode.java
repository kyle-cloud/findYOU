package rtree;

import java.util.ArrayList;
import java.util.List;
import rtree.Constants;

/**
 * @ClassName RTDirNode
 * @Description 非叶节点
 */
public class RTDirNode extends RTNode {
    /**
     * 孩子结点集
     */
    protected List<RTNode> children;

    // 构造函数
    public RTDirNode(RTree rtree, RTNode parent, int level) {
        super(rtree, parent, level); // 调用父类的构造函数
        children = new ArrayList<RTNode>(); // 新建一个RTNode类型的结点数组
    }

    /**
     * @param index
     * @return 对应索引下的孩子结点
     */
    public RTNode getChild(int index) {
        return children.get(index);
    }

    @Override
    /*-->选择叶子结点*/
    public RTDataNode chooseLeaf(Rectangle rectangle) {
        int index;

        switch (rtree.getTreeType()) {
        case Constants.RTREE_LINEAR:

        case Constants.RTREE_QUADRATIC:

        case Constants.RTREE_EXPONENTIAL:
            index = findLeastEnlargement(rectangle); // 获得面积增量最小的结点的索引
            break;
        case Constants.RSTAR:
            if (level == 1)// 即此结点指向叶节点
            {
                index = findLeastOverlap(rectangle); // 获得最小重叠面积的结点的索引
            } else {
                index = findLeastEnlargement(rectangle); // 获得面积增量最小的结点的索引
            }
            break;

        default:
            throw new IllegalStateException("Invalid tree type.");
        }

        insertIndex = index;// 记录插入路径的索引

        return getChild(index).chooseLeaf(rectangle); // 非叶子节点的chooseLeaf（）实现递归调用
    }

    /**
     * @param rectangle
     * @return -->返回最小重叠面积的结点的索引， 如果重叠面积相等则选择加入此Rectangle后面积增量更小的，
     *         如果面积增量还相等则选择自身面积更小的
     */
    private int findLeastOverlap(Rectangle rectangle) {
        float overlap = Float.POSITIVE_INFINITY;
        int sel = -1;

        for (int i = 0; i < usedSpace; i++) {
            RTNode node = getChild(i);
            float ol = 0; // 用于记录每个孩子的datas数据与传入矩形的重叠面积之和

            for (int j = 0; j < node.datas.length; j++) {
                // 将传入矩形与各个矩形重叠的面积累加到ol中，得到重叠的总面积
                ol += rectangle.intersectingArea(node.datas[j]);
            }
            if (ol < overlap) {
                overlap = ol;// 记录重叠面积最小的
                sel = i;// 记录第几个孩子的索引
            }
            // 如果重叠面积相等则选择加入此Rectangle后面积增量更小的,如果面积增量还相等则选择自身面积更小的
            else if (ol == overlap) {
                double area1 = datas[i].getUnionRectangle(rectangle).getArea() - datas[i].getArea();
                double area2 = datas[sel].getUnionRectangle(rectangle).getArea() - datas[sel].getArea();

                if (area1 == area2) {
                    sel = (datas[sel].getArea() <= datas[i].getArea()) ? sel : i;
                } else {
                    sel = (area1 < area2) ? i : sel;
                }
            }
        }
        return sel;
    }

    /**
     * @param rectangle
     * @return -->面积增量最小的结点的索引，如果面积增量相等则选择自身面积更小的
     */
    private int findLeastEnlargement(Rectangle rectangle) {
        double area = Double.POSITIVE_INFINITY; // double类型的正无穷
        int sel = -1;

        for (int i = 0; i < usedSpace; i++) {
            // 增量enlargement = 包含（datas[i]里面存储的矩形与查找的矩形）的最小矩形的面积 -
            // datas[i]里面存储的矩形的面积
            double enlargement = datas[i].getUnionRectangle(rectangle).getArea() - datas[i].getArea();
            if (enlargement < area) {
                area = enlargement; // 记录增量
                sel = i; // 记录引起增量的【包含（datas[i]里面存储的矩形与查找的矩形）的最小矩形】里面的datas[i]的索引
            } else if (enlargement == area) {
                sel = (datas[sel].getArea() < datas[i].getArea()) ? sel : i;
            }
        }

        return sel;
    }

    /**
     * --> 插入新的Rectangle后从插入的叶节点开始向上调整RTree，直到根节点
     * 
     * @param node1
     *            引起需要调整的孩子结点
     * @param node2
     *            分裂的结点，若未分裂则为null
     */
    public void adjustTree(RTNode node1, RTNode node2) {
        // 先要找到指向原来旧的结点（即未添加Rectangle之前）的条目的索引
        datas[insertIndex] = node1.getNodeRectangle();// 先用node1覆盖原来的结点
        children.set(insertIndex, node1);// 替换旧的结点

        if (node2 != null) {
            insert(node2);// 插入新的结点

        }
        // 还没到达根节点
        else if (!isRoot()) {
            RTDirNode parent = (RTDirNode) getParent();
            parent.adjustTree(this, null);// 向上调整直到根节点
        }
    }

    /**
     * -->非叶子节点插入
     * 
     * @param node
     * @return 如果结点需要分裂则返回true
     */
    protected boolean insert(RTNode node) {
        // 已用结点小于树的节点容量，不需分裂，只需插入以及调整树
        if (usedSpace < rtree.getNodeCapacity()) {
            datas[usedSpace++] = node.getNodeRectangle();
            children.add(node);// 新加的
            node.parent = this;// 新加的
            RTDirNode parent = (RTDirNode) getParent();
            if (parent != null) // 不是根节点
            {
                parent.adjustTree(this, null);
            }
            return false;
        } else {// 非叶子结点需要分裂
            RTDirNode[] a = splitIndex(node);
            RTDirNode n = a[0];
            RTDirNode nn = a[1];

            if (isRoot()) {
                // 新建根节点，层数加1
                RTDirNode newRoot = new RTDirNode(rtree, Constants.NULL, level + 1);

                // 把两个分裂的结点n和nn添加到根节点
                newRoot.addData(n.getNodeRectangle());
                newRoot.addData(nn.getNodeRectangle());

                newRoot.children.add(n);
                newRoot.children.add(nn);

                // 设置两个分裂的结点n和nn的父节点
                n.parent = newRoot;
                nn.parent = newRoot;

                // 最后设置rtree的根节点
                rtree.setRoot(newRoot);// 新加的
            } else {
                // 如果不是根结点，向上调整树
                RTDirNode p = (RTDirNode) getParent();
                p.adjustTree(n, nn);
            }
        }
        return true;
    }

    /**
     * -->非叶子结点的分裂
     * 
     * @param node
     * @return
     */
    private RTDirNode[] splitIndex(RTNode node) {
        int[][] group = null;
        switch (rtree.getTreeType()) {
        case Constants.RTREE_LINEAR:
            break;
        case Constants.RTREE_QUADRATIC:
            group = quadraticSplit(node.getNodeRectangle());
            children.add(node);// 新加的
            node.parent = this;// 新加的
            break;
        case Constants.RTREE_EXPONENTIAL:
            break;
        case Constants.RSTAR:
            break;
        default:
            throw new IllegalStateException("Invalid tree type.");
        }
        // 新建两个非叶子节点
        RTDirNode index1 = new RTDirNode(rtree, parent, level);
        RTDirNode index2 = new RTDirNode(rtree, parent, level);

        int[] group1 = group[0];
        int[] group2 = group[1];
        // 为index1添加数据和孩子
        for (int i = 0; i < group1.length; i++) {
            index1.addData(datas[group1[i]]);
            index1.children.add(this.children.get(group1[i]));// 新加的
            // 让index1成为其父节点
            this.children.get(group1[i]).parent = index1;// 新加的
        }
        for (int i = 0; i < group2.length; i++) {
            index2.addData(datas[group2[i]]);
            index2.children.add(this.children.get(group2[i]));// 新加的
            this.children.get(group2[i]).parent = index2;// 新加的
        }
        return new RTDirNode[] { index1, index2 };
    }

    @Override
    // 寻找叶子
    public RTDataNode findLeaf(Rectangle rectangle) {
        for (int i = 0; i < usedSpace; i++) {
            if (datas[i].enclosure(rectangle)) {
                deleteIndex = i;// 记录搜索路径
                RTDataNode leaf = children.get(i).findLeaf(rectangle); // 递归查找
                if (leaf != null)
                    return leaf;
            }
        }
        return null;
    }

}