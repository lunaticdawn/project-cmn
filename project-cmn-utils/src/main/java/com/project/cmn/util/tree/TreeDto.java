package com.project.cmn.util.tree;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 트리 구조의 자료구조를 만들기 위한 DTO 객체
 */
@Getter
@Setter
@ToString
public class TreeDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 노드의 아이디
	 */
	private String id;

	/**
	 * 부모 노드의 아이디
	 */
	private String parentId;

	/**
	 * 자식 노드들
	 */
	private List<TreeDto> children;

	/**
	 * 자식 노드를 추가
	 *
	 * @param treeDto {@link TreeDto}
	 */
	public void add(TreeDto treeDto) {
		if (children == null) {
			children = new ArrayList<>();
		}

		children.add(treeDto);
	}
}
