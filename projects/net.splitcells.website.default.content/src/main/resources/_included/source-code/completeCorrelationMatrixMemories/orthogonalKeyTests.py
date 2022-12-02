#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR MIT
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import unittest
from completeCorrelationMatrixMemories import CompleteCorrelationMatrixMemory
from testingArtifacts import floatTuple, difference, images3x3
from random import uniform, sample
class OrthogonalKeyTests(unittest.TestCase):
	def testAssymetricOrthogonalKeys(self):
		self.__testOrthogonalKeys({
			(3,0) : (3,1,4.5),
			(0,-7) : (-3,-1,99)
		})
	def testSimpleOrthogonalKeys1(self):
		self.__testOrthogonalKeys({
			(1,0) : (3,1,4.5),
			(0,1) : (-3,-1,99)
		})
	def testSimpleOrthogonalKeys2(self):
		self.__testOrthogonalKeys({
			(2,0) : (3,1,4.5),
			(0,2) : (-3,-1,99)
		})
	def testSimpleOrthogonalKeys4(self):
		self.__testOrthogonalKeys({
			(4,0) : (3,1,4.5),
			(0,4) : (-3,-1,99)
		})
	def testSimpleOrthogonalKeys8(self):
		self.__testOrthogonalKeys({
			(8,0) : (3,1,4.5),
			(0,8) : (-3,-1,99)
		})
	def testSimpleOrthogonalKeysRandom(self):
		self.__testOrthogonalKeys({
			(uniform(-1000, 1000),0) : (3,1,4.5), # TODO Random chosen magic constants
			(0,uniform(-1000, 1000)) : (-3,-1,99) # TODO Random chosen magic constants
		})
	def testSimpleOrthogonalKeys9ZeroTwo(self):
		values = sample(list(images3x3.values()), 9)
		mapping = {}
		for i in range(9):
			orthogonalKey = [0]*9
			orthogonalKey[i] = 2
			orthogonalKey = tuple(orthogonalKey)
			mapping[orthogonalKey] = values[i]
		self.__testOrthogonalKeys(mapping)
	def testSimpleOrthogonalKeys9uniform(self):
		values = sample(list(images3x3.values()), 9)
		mapping = {}
		for i in range(9):
			orthogonalKey = [0]*9
			orthogonalKey[i] = uniform(-1000, 1000) # TODO Random chosen magic constants
			orthogonalKey = tuple(orthogonalKey)
			mapping[orthogonalKey] = values[i]
		self.__testOrthogonalKeys(mapping)
	def __testOrthogonalKeys(self, mapping):
		testSubject = CompleteCorrelationMatrixMemory(mapping)
		for key in mapping:
			recall = testSubject.recall(key)
			expectedRecall = floatTuple(mapping[key])
			for i in range(len(recall)):
				self.assertAlmostEqual(recall[i], expectedRecall[i])