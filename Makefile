# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.

# This is mainly used for compatibility purposes.
build :
	$(chmod +x ./bin/*)
	@echo Nothing has to be done in order to build this project.
	@echo You can just execute '"'make install'"'.
	@echo Sudo etc. is not required.
install :
	./bin/install
