/*
 *  Copyright (C) 2010-2012 Axel Morgner, structr <structr@structr.org>
 * 
 *  This file is part of structr <http://structr.org>.
 * 
 *  structr is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  structr is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.structr.core.traversal;

import org.structr.core.predicate.TypePredicate;
import java.util.Collections;
import java.util.List;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.RelationshipType;
import org.structr.core.entity.AbstractNode;
import org.structr.core.notion.Notion;
import org.structr.core.notion.ObjectNotion;

/**
 *
 * @author Christian Morgner
 */
public class RandomRelatedNodes extends AbstractNodeCollector {

	private int count = 0;
	
	public RandomRelatedNodes(RelationshipType relType, Direction direction, Class resultType, int maxDepth, int count) {
		this(relType, direction, resultType, new ObjectNotion(), maxDepth, count);
	}

	public RandomRelatedNodes(RelationshipType relType, Direction direction, Class resultType, Notion notion, int maxDepth, int count) {

		super(relType, direction, maxDepth);

		// add type predicate
		this.addPredicate(new TypePredicate(resultType.getSimpleName()));

		this.setNotion(notion);
		this.count = count;
	}

	@Override
	public List transformResult(List<AbstractNode> result) {

		// random ordering
		Collections.shuffle(result);
		
		// truncate list to length count
		return result.subList(0, Math.min(result.size(), count));
	}

	@Override
	public void cleanup() {
	}
}
