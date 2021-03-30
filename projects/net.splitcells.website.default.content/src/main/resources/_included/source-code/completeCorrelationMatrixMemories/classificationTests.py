#!/usr/bin/env python3
"""This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this
file, You can obtain one at http://mozilla.org/MPL/2.0/.

Based on the paper "Correlation Matrix Memories" by TEUVO KOHONEN.
"""
__author__ = 'Mārtiņš Avots'
from completeCorrelationMatrixMemories import CompleteCorrelationMatrixMemory
from unittest import TestCase, expectedFailure
from testingArtifacts import floatTuple, difference, images3x3
class ClassificationTests(TestCase):
	# TODO high resolution image tests
	def testClassificationOf2Images(self):
		self.__testOrthogonalKeys({
			images3x3["cross"] : (0,),
			images3x3["zero"] : (2,),
		})
	def testClassificationOf2OtherImages(self):
		self.__testOrthogonalKeys({
			images3x3["cross"] : (0,),
			images3x3["zero"] : (2,),
		})
	@expectedFailure
	def testClassificationOf9Images(self):
		"""It is not expected to work, as the keys are most propably not orthogonal"""
		self.__testOrthogonalKeys({
			images3x3["cross"] : (0,),
			images3x3["vertical"] : (1,),
			images3x3["horizontal"] : (2,),
			images3x3["zero"] : (3,),
			images3x3["one"] : (4,)
		})
	def __testOrthogonalKeys(self, mapping):
		testSubject = CompleteCorrelationMatrixMemory(mapping)
		for key in mapping:
			self.assertEqual(mapping[key][0], self.__nearestNumber(mapping.values(), testSubject.recall(key)[0]))
	def __nearestNumber(self, candidates, number):
		"""Candidate is an tuple with 1 element."""
		diff = 1000000000 # TODO magic number
		candidate = None
		for i in candidates:
			currentDiff = abs(i[0] - number)
			if currentDiff < diff:
				diff = currentDiff
				candidate = i[0]
		return candidate