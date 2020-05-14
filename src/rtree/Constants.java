package rtree;

import rtree.RTNode;

public class Constants {
    public static final int MAX_NUMBER_OF_ENTRIES_IN_NODE = 20;// 结点中的最大条目数
    public static final int MIN_NUMBER_OF_ENTRIES_IN_NODE = 8;// 结点中的最小条目数

    public static final int RTDataNode_Dimension = 2;

    /** Available RTree variants. */ // 树的类型常量
    public static final int RTREE_LINEAR = 0; // 线性
    public static final int RTREE_QUADRATIC = 1; // 二维
    public static final int RTREE_EXPONENTIAL = 2; // 多维
    public static final int RSTAR = 3; // 星型

    public static final int NIL = -1;
    public static final RTNode NULL = null;
}