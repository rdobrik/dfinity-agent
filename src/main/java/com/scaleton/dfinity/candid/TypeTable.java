/*
 * Copyright 2021 Exilor Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.scaleton.dfinity.candid;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import com.scaleton.dfinity.candid.types.Opcode;

public final class TypeTable {
	// Raw value of the type description table
	List<List<Integer>> table;

	// Value types for deserialization
	Queue<Integer> types;

	// The front of current_type queue always points to the type of the value we are
	// deserializing.
	Deque<Integer> currentType;

	TypeTable(List<List<Integer>> table, Queue<Integer> types, Deque<Integer> currentType) {
		this.table = table;
		this.types = types;
		this.currentType = currentType;

	}

	// Parse the type table and return the remaining bytes
	public static TypeTableResponse fromBytes(byte[] input) {
		List<List<Integer>> table = new ArrayList<List<Integer>>();

		Queue<Integer> types = new LinkedList<Integer>();

		Bytes bytes = new Bytes(input);

		bytes.parseMagic();

		int len = bytes.leb128Read();

		for (int i = 0; i < len; i++) {
			List<Integer> buf = new ArrayList<Integer>();

			Integer ty = bytes.leb128ReadSigned();

			buf.add(ty);
			if (ty == Opcode.OPT.value || ty == Opcode.VEC.value) {
				ty = bytes.leb128ReadSigned();
				validateTypeRange(ty, len);
				buf.add(ty);
			}else if (ty == Opcode.RECORD.value || ty == Opcode.VARIANT.value) {
				Integer objLen = bytes.leb128Read();
				buf.add(objLen);
				
				Optional<Integer> prevHash = Optional.empty();
				
				for (int j = 0; j < objLen; j++)
				{
					Integer hash = bytes.leb128Read();
					
					if(prevHash.isPresent())
					{
						if(prevHash.get() >= hash)
							throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Field id collision or not sorted");
					}
					
					prevHash = Optional.of(hash);				
					buf.add(hash);
					ty = bytes.leb128ReadSigned();
					validateTypeRange(ty, len);
					buf.add(ty);
				}
			}
			else {
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("Unsupported op_code %d in type table", ty));
			}
			
			table.add(buf);
		}

		len = bytes.leb128Read();

		for (int i = 0; i < len; i++) {
			int ty = bytes.leb128ReadSigned();
			validateTypeRange(ty, table.size());
			types.add(ty);
		}

		TypeTable typeTable = new TypeTable(table, types, new LinkedList<Integer>());

		TypeTableResponse response = new TypeTableResponse();

		response.typeTable = typeTable;

		response.data = new byte[bytes.data.remaining()];
		bytes.data.get(response.data);

		return response;

	}

	static boolean isPrimitiveType(int ty) {
		return (ty < 0 && (ty >= -17 || ty == -24));
	}

	static void validateTypeRange(int ty, int len) {
		if (ty >= 0 && (ty < len || isPrimitiveType(ty)))
			return;
		else
			CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("Unknown type %d", ty));
	}

	Integer popCurrentType() {

		Integer type = this.currentType.poll();

		if (type != null)
			return type;
		else
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Empty current_type");

	}

	Integer peekCurrentType() {

		Integer type = this.currentType.peek();

		if (type != null)
			return type;
		else
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Empty current_type");

	}

	Opcode rawValueToOpcode(Integer value) {
		if (value >= 0 && value < this.table.size())
			value = this.table.get(value).get(0);

		return Opcode.from(value);
	}

	// Pop type opcode from the front of currentType.
	// If the opcode is an index (>= 0), we push the corresponding entry from table,
	// to currentType queue, and pop the opcode from the front.

	// Same logic as parseType, but not poping the currentType queue.

	Opcode parseType() {
		Integer op = this.popCurrentType();

		if (op >= 0 && op < this.table.size()) {
			List<Integer> ty = this.table.get(op);

			Iterator<Integer> it = ty.listIterator();

			while (it.hasNext())
				this.currentType.add(it.next());

			op = this.popCurrentType();

		}

		return Opcode.from(op);

	}

	// Same logic as parseType, but not poping the currentType queue.
	Opcode peekType() {
		Integer op = this.peekCurrentType();

		return this.rawValueToOpcode(op);
	}

	// Check if currentType matches the provided type
	void checkType(Opcode expected) {
		Opcode wireType = this.parseType();

		if (wireType != expected)
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String
					.format("Type mismatch. Type on the wire: %d; Expected type: %d", wireType.value, expected.value));
	}

}
