#!/usr/bin/env bash
psql -h 127.0.0.1 -U fuzzy -f drop-schema.sql
psql -h 127.0.0.1 -U fuzzy -f create-schema.sql
psql -h 127.0.0.1 -U fuzzy -f orderings.sql
