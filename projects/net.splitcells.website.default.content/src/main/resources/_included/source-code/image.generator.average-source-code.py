#!/usr/bin/env python3
"""This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this
file, You can obtain one at http://mozilla.org/MPL/2.0/."""
__author__ = 'Mārtiņš Avots'
import argparse
from os import walk, sep
import os
from PIL import \
	Image \
	,ImageDraw \
	,ImageFont \
	,ImageFilter
from random import \
	randint \
	,shuffle
import random
def color(arg):
	try:
		(r, g, b) = map(int, arg.split(','))
		return (r, g, b)
	except:
		raise argparse.ArgumentTypeError('Color must be defined as "r,g,b" but is instead defined as: ' + arg)
def config():
	parser = argparse.ArgumentParser()
	parser.add_argument('input_folder', type=str)
	parser.add_argument('output_file', type=str)
	parser.add_argument('space_between_lines', nargs='?', type=int, default=10)
	parser.add_argument('text_starting_pos', nargs='?', type=int,
						default=(10, 10))
	parser.add_argument('image_size', nargs='?', type=int, default=(4000,9000))
	parser.add_argument('gaussian_blur', nargs='?', type=int, default=2)
	parser.add_argument('max_files_on_top_layer', nargs='?', type=int,
						default=50,
						help='Maximum number of files used ' +
							 'in order to generate a temporary top layer.')
	parser.add_argument('max_files_used_for_result', nargs='?', type=int,
						default=200)
	parser.add_argument('standard_alpha_val', nargs='?', type=int, default=125)
	parser.add_argument('output_file_type', nargs='?', type=str, default='JPEG')
	parser.add_argument('indent_width', nargs='?', type=int, default='10',
						help='Width of 1 indent level, which is used for placing objects on the resulting image, defined by the number of white spaces.')
	parser.add_argument('max_indent_level', nargs='?', type=int, default='6',
						help='Maximal level of indent used for drawing objects on image.')
	parser.add_argument('max_additional_empty_lines', nargs='?', type=int,
						default='50',
						help='Maximal number of empty lines inserted at the beginning ' +
							 'of each text that is drawn onto the resulting image.'
						)
	parser.add_argument('--colors', nargs='*', type=color, default=[
		(255,0,0), # red
		(0,255,0), # green
		(0,0,255), # blue
		(0,255,255), # cyan
		(0,0,0), # black
		(255,255,0), # yellow
		(255,0,255)] # purple
						)
	rVal = parser.parse_args()
	rVal.input_files = all_file_paths(rVal.input_folder)
	return rVal
class image_layered():
	def __init__(self, config):
		self.image_stack = []
		self.__add_layer__(config.image_size, 10)
	def add_image(self, config): # RENAME add_layer
		self.__add_layer__(config.image_size, config.standard_alpha_val)
	def __add_layer__(self, image_size, costume_alpha_val):
		im = Image.new('RGBA', image_size, (0, 0, 0, costume_alpha_val))
		draw = ImageDraw.Draw(im)
		self.image_stack.append((im, draw))
	def merge_top_layer(self):
		top = self.image_stack.pop(0)
		below_top = self.image_stack.pop(0)
		merge = Image.alpha_composite(below_top[0], top[0])
		self.image_stack.append((merge, ImageDraw.Draw(merge)))
	def merge_layers(self):
		base = self.image_stack.pop(0)[0]
		for ele in self.image_stack:
			base = Image.alpha_composite(base, ele[0])
		self.image_stack = [[base, ImageDraw.Draw(base)]]
def all_file_paths(folder):
	rVal = []
	for (dir_path, dir_names, file_names) in walk(folder):
		for file_name in file_names:
			rVal.append(os.path.join(dir_path, file_name))
	return rVal
def draw_averageSourceCode(config, image_layered):
	image_layered.add_image(config)
	used_files_on_top_layer = 0
	shuffle(config.input_files)
	if len(config.input_files) >= config.max_files_used_for_result:
		considered_input_files = random.sample(config.input_files,
											   config.max_files_used_for_result)
	else:
		considered_input_files = config.input_files
	for file_path in considered_input_files:
		try:
			with open(file_path, "r", encoding="utf8") as file_content:
				used_files_on_top_layer += 1
				line_counter = 0
				processed_file_content = randint(0, config.max_additional_empty_lines) * ["\n"]
				for line in file_content:
					processed_file_content.append(line)
				for line in processed_file_content:
					line_counter += 1
					text_position = (config.text_starting_pos[0]
									 ,config.text_starting_pos[1] + line_counter
									 * config.space_between_lines)
					image_layered.image_stack[-1][1].text(
						text_position,
						(config.indent_width * randint(0, config.max_indent_level)) * " " + line,
						fill=random.choice(config.colors) + (config.standard_alpha_val,)
					)
				if used_files_on_top_layer >= config.max_files_on_top_layer:
					used_files_on_top_layer = 0
					image_layered.merge_top_layer()
					image_layered.add_image(config)
		except (UnicodeEncodeError, UnicodeDecodeError):
			print("This file is not used: " + str(file_path))
	if len(image_layered.image_stack) != 1:
		image_layered.merge_top_layer()
def savePicture(config, image_layered):
	image_layered.merge_layers()
	im = image_layered.image_stack[0][0]
	im = im.filter(ImageFilter.GaussianBlur(config.gaussian_blur))
	try:
		im.save(config.output_file, config.output_file_type)
	except IOError:
		print("Failed to save resulting picture at:" + config.output_file)
if __name__ == "__main__":
	config = config()
	rVal = image_layered(config)
	draw_averageSourceCode(config, rVal)
	savePicture(config, rVal)
