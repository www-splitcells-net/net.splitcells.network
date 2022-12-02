#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR MIT
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

from testingArtifacts import floatTuple
import unittest
class CompleteCorrelationMatrixMemory():
	def __init__(self, keyValuePairs):
		for key in keyValuePairs:
			self.columns = len(key)
			self.rows = len(keyValuePairs[key])
			break
		self.matrix = [[0. for i in range(self.columns)] for j in range(self.rows)]
		keyValuePairs = self.convertKeyValuePairs(keyValuePairs)
		for iRow in range(self.rows):
			for iColumn in range(self.columns):
				for key in keyValuePairs:
					c = self.c(key) # Deviation from TEUVO's paper
					value = keyValuePairs[key]
					self.matrix[iRow][iColumn] += c * key[iColumn] * value[iRow]
	def convertKeyValuePairs(self, keyValuePairs):
		rVal = dict()
		for key in keyValuePairs:
			value = keyValuePairs[key]
			key = tuple(floatTuple(key))
			value = floatTuple(value)
			rVal[key] = value
		return rVal
	def c(self, vector):
		rBase = 0.
		for i in vector:
			rBase += i ** 2
		return 1. / rBase
	def recall(self, key):
		key = floatTuple(key)
		value = [0.]*self.rows
		for iRow in range(len(value)):
			for iColumn in range(len(key)):
				value[iRow]
				self.matrix[iRow][iColumn]
				key[iColumn]
				value[iRow] += self.matrix[iRow][iColumn] * key[iColumn]
		return floatTuple(value)
if __name__ == "__main__":
	unittest.main()