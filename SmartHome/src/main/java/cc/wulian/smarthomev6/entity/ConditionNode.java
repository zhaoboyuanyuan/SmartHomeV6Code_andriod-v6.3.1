package cc.wulian.smarthomev6.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionNode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 实现最简单符合协议的二叉树类 or / \ or D / \ or C / \ A B
	 */
	private ConditionNode parent;
	private ConditionNode left;
	private ConditionNode right;
	private String userString;

	private static Map<String, String> conditionMap = new HashMap<String, String>();
	static {
		conditionMap.put("or", "or");
		conditionMap.put("and", "and");
	}

	public ConditionNode() {
	}

	public ConditionNode(String userString) {
		this.userString = userString;
	}

	public ConditionNode getParent() {
		return parent;
	}

	public void setParent(ConditionNode parent) {
		this.parent = parent;
	}

	public ConditionNode getLeft() {
		return left;
	}

	public void setLeft(ConditionNode left) {
		this.left = left;
	}

	public ConditionNode getRight() {
		return right;
	}

	public void setRight(ConditionNode right) {
		this.right = right;
	}

	public String getUserString() {
		return userString;
	}

	// 是否为根结点
	public boolean isRoot() {
		return getParent() == null;
	}

	public void setUserString(String userString) {
		this.userString = userString;
	}

	// 转成String类型
	private void toTreeString(List<String> list) {
		list.add(this.userString.toString());
		if (left != null)
			left.toTreeString(list);
		if (right != null)
			right.toTreeString(list);
	}

	// 转成List类型
	public List<String> toTreeStrings() {
		List<String> list = new ArrayList<String>();
		toTreeString(list);
		return list;
	}

	// 得到根结点
	public ConditionNode getRoot(ConditionNode node) {
		if (node.isRoot())
			return node;
		else {
			return getRoot(node.getParent());
		}
	}

	// 添加条件(or,A)
	public ConditionNode addCondition(String nodeConditionString,
			String nodeContentString) {
		ConditionNode nodeRoot = new ConditionNode(nodeConditionString);
		ConditionNode nodeContent = new ConditionNode(nodeContentString);
		this.setParent(nodeRoot);
		nodeContent.setParent(nodeRoot);
		nodeRoot.setLeft(this);
		nodeRoot.setRight(nodeContent);
		return nodeRoot;
	}

	// 查询某个结点
	public ConditionNode search(String nodeConentString) {
		return search(this, nodeConentString);
	}

	private ConditionNode search(ConditionNode node, String nodeConentString) {
		ConditionNode result = null;
		if (node.getUserString().equals(nodeConentString)) {
			result = node;
		}
		if (result == null && node.getLeft() != null) {
			result = search(node.getLeft(), nodeConentString);
		}
		if (result == null && node.getRight() != null) {
			result = search(node.getRight(), nodeConentString);
		}
		return result;
	}

	// 删除三种不同的结点
	public ConditionNode deleteCondition(String nodeContentString) {
		ConditionNode nodeContent = search(nodeContentString);
		ConditionNode currentNode = nodeContent.getParent();
		if (currentNode != null) {
			if (currentNode.isRoot()) {
				if(currentNode.getLeft() == nodeContent){
					currentNode.getRight().setParent(null);
					return currentNode.getRight();
				}else{
					currentNode.getLeft().setParent(null);
					return currentNode.getLeft();
				}
			}else if (currentNode.getLeft() == nodeContent) {
				currentNode.getParent().setLeft(currentNode.getRight());
				currentNode.getRight().setParent(currentNode.getParent());
			}else{
				currentNode.getParent().setLeft(currentNode.getLeft());
				currentNode.getLeft().setParent(currentNode.getParent());
			}
			return getRoot(currentNode.getParent());
		}else{
			return null;
		}
	}

	// 更新某个结点
	public ConditionNode updateCondition(String nodeContentString,
			String newContentString) {
		
		ConditionNode nodeContent = search(nodeContentString);
		ConditionNode newContentRoot = new ConditionNode(newContentString);
		if(nodeContent.getParent() != null){
			ConditionNode currentNode = nodeContent.getParent();
			if(currentNode.getLeft() == nodeContent){
				if(nodeContent.getLeft() != null && nodeContent.getRight() != null){
					newContentRoot.setLeft(nodeContent.getLeft());
					newContentRoot.setRight(nodeContent.getRight());
					nodeContent.getLeft().setParent(newContentRoot);
					nodeContent.getRight().setParent(newContentRoot);
				}
				currentNode.setLeft(newContentRoot);
				currentNode.getLeft().setParent(currentNode);
			}else if(currentNode.getRight() == nodeContent){
				currentNode.setRight(newContentRoot);
				currentNode.getRight().setParent(currentNode);
			}
			return getRoot(newContentRoot);
		}else{
			if(nodeContent.getLeft() != null && nodeContent.getRight() != null){
				newContentRoot.setLeft(nodeContent.getLeft());
				newContentRoot.setRight(nodeContent.getRight());
				nodeContent.getLeft().setParent(newContentRoot);
				nodeContent.getRight().setParent(newContentRoot);
			}
			return getRoot(newContentRoot);
		}
	}

	// 生成树的结构
	public static ConditionNode createTree(List<String> Strings) {
		ConditionNode root = null;
		ConditionNode currentNode = null;
		for (String o : Strings) {
			ConditionNode n = new ConditionNode(o);
			if (root == null) {
				root = n;
				currentNode = root;
			} else {
				n.setParent(currentNode);
				if (conditionMap.containsKey(o)) {
					currentNode.setLeft(n);
					n.setParent(currentNode);
					currentNode = n;
				} else {
					if (currentNode.getLeft() == null) {
						currentNode.setLeft(n);
						n.setParent(currentNode);
					} else {
						currentNode.setRight(n);
						n.setParent(currentNode);
						currentNode = currentNode.getParent();
					}
				}
			}
		}
		return root;
	}

}
