/*
 *  Copyright (C) 2010-2012 Axel Morgner
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

package org.structr.rest.adapter;

import org.structr.core.GraphObjectGSONAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import org.structr.core.GraphObject;
import org.structr.core.Value;
import org.structr.core.Result;
import org.structr.core.PropertySet.PropertyFormat;
import org.structr.core.entity.AbstractNode;

/**
 * Controls deserialization of property sets.
 *
 * @author Christian Morgner
 */
public class ResultGSONAdapter implements JsonSerializer<Result>, JsonDeserializer<Result> {

	private GraphObjectGSONAdapter graphObjectGsonAdapter = null;

	public ResultGSONAdapter(PropertyFormat propertyFormat, Value<String> propertyView, String idProperty) {
		this.graphObjectGsonAdapter = new GraphObjectGSONAdapter(propertyFormat, propertyView, idProperty);
	}

	@Override
	public JsonElement serialize(Result src, Type typeOfSrc, JsonSerializationContext context) {

		long t0 = System.nanoTime();
		
		JsonObject result = new JsonObject();

		// result fields in alphabetical order
		List<? extends GraphObject> results = src.getResults();
		Integer page = src.getPage();
		Integer pageCount = src.getPageCount();
		Integer pageSize = src.getPageSize();
		String queryTime = src.getQueryTime();
		Integer resultCount = src.getRawResultCount();
		String searchString = src.getSearchString();
		String sortKey = src.getSortKey();
		String sortOrder = src.getSortOrder();

		if(page != null) {
			result.add("page", new JsonPrimitive(page));
		}

		if(pageCount != null) {
			result.add("page_count", new JsonPrimitive(pageCount));
		}

		if(pageSize != null) {
			result.add("page_size", new JsonPrimitive(pageSize));
		}

		if(queryTime != null) {
			result.add("query_time", new JsonPrimitive(queryTime));
		}

		if(resultCount != null) {
			result.add("result_count", new JsonPrimitive(resultCount));
		}

		if(results != null) {

			if(results.isEmpty()) {

				result.add("result", new JsonArray());

			} else if(src.isPrimitiveArray()) {

				JsonArray resultArray = new JsonArray();
				for(GraphObject graphObject : results) {
					Object value = graphObject.getProperty(AbstractNode.Key.uuid.name());	// FIXME: UUID key hard-coded, use variable in Result here!
					if(value != null) {
						resultArray.add(new JsonPrimitive(value.toString()));
					}
				}

				result.add("result", resultArray);


			} else {

				if (results.size() > 1 && !src.isCollection()){
					throw new IllegalStateException(src.getClass().getSimpleName() + " is not a collection resource, but result set has size " + results.size());
				}

				if(src.isCollection()) {

					// serialize list of results
					JsonArray resultArray = new JsonArray();
					for(GraphObject graphObject : results) {
						resultArray.add(graphObjectGsonAdapter.serialize(graphObject, GraphObject.class, context));
					}

					result.add("result", resultArray);

				} else {

					// use GraphObject adapter to serialize single result
					result.add("result", graphObjectGsonAdapter.serialize(results.get(0), GraphObject.class, context));
				}
			}
		}

		if(searchString != null) {
			result.add("search_string", new JsonPrimitive(searchString));
		}

		if(sortKey != null) {
			result.add("sort_key", new JsonPrimitive(sortKey));
		}

		if(sortOrder != null) {
			result.add("sort_order", new JsonPrimitive(sortOrder));
		}
		
		DecimalFormat decimalFormat = new DecimalFormat("0.000000000", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		result.add("serialization_time", new JsonPrimitive(decimalFormat.format((System.nanoTime() - t0) / 1000000000.0)));

		return result;
	}

	@Override
	public Result deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return null;
	}

	public GraphObjectGSONAdapter getGraphObjectGSONAdapter() {
		return graphObjectGsonAdapter;
	}
}
