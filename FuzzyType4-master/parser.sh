#!/bin/bash
cd JSqlParser && \
	ant parser jar && \
	cd .. && mv JSqlParser/dist/QbosSqlParser.jar FuzzyDB/libraries && \
	cd FuzzyDB && ant jar
