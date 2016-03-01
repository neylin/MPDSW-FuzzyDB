# para limpiar el cache se debe hacer como root
sync
echo 3 > /proc/sys/vm/drop_caches
echo 'Base de datos World con volumen alto' > resultadoWVA.txt
echo ' Caso select2-1' >> resultadoWVA.txt
# /usr/bin/time -f "\n%E elapsed,\n%U user,\n%S system,\n%M memory\n%x status" head -n 10000
(time java -jar dist/FuzzyDB.jar < select2-1.sql)  2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso select2-2' >> resultadoWVA.txt
(time java -jar dist/FuzzyDB.jar < select2-2.sql)  2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso select2-3' >> resultadoWVA.txt
(time java -jar dist/FuzzyDB.jar < select2-3.sql)  2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso select2-4' >> resultadoWVA.txt
(time java -jar dist/FuzzyDB.jar < select2-4.sql)  2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso groupby2-1' >> resultadoWVA.txt
(time java -jar dist/FuzzyDB.jar < groupby2-1.sql)  2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso groupby2-2' >> resultadoWVA.txt
(time java -jar dist/FuzzyDB.jar < groupby2-2.sql)  2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso groupby2-3' >> resultadoWVA.txt
(time java -jar dist/FuzzyDB.jar < groupby2-3.sql)  2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso groupby2-4' >> resultadoWVA.txt
(time java -jar dist/FuzzyDB.jar < groupby2-4.sql)  2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso orderby2-1' >> resultadoWVA.txt
(time java -jar dist/FuzzyDB.jar < orderby2-1.sql)  2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso orderby2-2' >> resultadoWVA.txt
(time java -jar dist/FuzzyDB.jar < orderby2-2.sql)  2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso orderby2-3' >> resultadoWVA.txt
(time java -jar dist/FuzzyDB.jar < orderby2-3.sql)  2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso orderby2-4' >> resultadoWVA.txt
(time java -jar dist/FuzzyDB.jar < orderby2-4.sql)  2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso postgres2-1' >> resultadoWVA.txt
(time sudo -u fuzzy psql fuzzy < postgres2-1.sql) 2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso postgres2-2' >> resultadoWVA.txt
(time sudo -u fuzzy psql fuzzy < postgres2-2.sql) 2>> resultadoWVA.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso postgres2-3' >> resultadoWVA.txt
(time sudo -u fuzzy psql fuzzy < postgres2-3.sql) 2>> resultadoWVA.txt