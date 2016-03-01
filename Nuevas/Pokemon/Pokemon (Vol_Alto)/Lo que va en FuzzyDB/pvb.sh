# para limpiar el cache se debe hacer como root
sync
echo 3 > /proc/sys/vm/drop_caches
echo 'Base de datos Pokemon con volumen bajo' > resultadoPVB.txt
echo ' Caso select1-1' >> resultadoPVB.txt
# /usr/bin/time -f "\n%E elapsed,\n%U user,\n%S system,\n%M memory\n%x status" head -n 10000
(time java -jar dist/FuzzyDB.jar < select1-1.sql)  2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso select1-2' >> resultadoPVB.txt
(time java -jar dist/FuzzyDB.jar < select1-2.sql)  2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso select1-3' >> resultadoPVB.txt
(time java -jar dist/FuzzyDB.jar < select1-3.sql)  2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso select1-4' >> resultadoPVB.txt
(time java -jar dist/FuzzyDB.jar < select1-4.sql)  2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso groupby1-1' >> resultadoPVB.txt
(time java -jar dist/FuzzyDB.jar < groupby1-1.sql)  2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso groupby1-2' >> resultadoPVB.txt
(time java -jar dist/FuzzyDB.jar < groupby1-2.sql)  2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso groupby1-3' >> resultadoPVB.txt
(time java -jar dist/FuzzyDB.jar < groupby1-3.sql)  2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso groupby1-4' >> resultadoPVB.txt
(time java -jar dist/FuzzyDB.jar < groupby1-4.sql)  2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso orderby1-1' >> resultadoPVB.txt
(time java -jar dist/FuzzyDB.jar < orderby1-1.sql)  2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso orderby1-2' >> resultadoPVB.txt
(time java -jar dist/FuzzyDB.jar < orderby1-2.sql)  2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso orderby1-3' >> resultadoPVB.txt
(time java -jar dist/FuzzyDB.jar < orderby1-3.sql)  2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso orderby1-4' >> resultadoPVB.txt
(time java -jar dist/FuzzyDB.jar < orderby1-4.sql)  2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso postgres1-1' >> resultadoPVB.txt
(time sudo -u fuzzy psql fuzzy < postgres1-1.sql) 2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso postgres1-2' >> resultadoPVB.txt
(time sudo -u fuzzy psql fuzzy < postgres1-2.sql) 2>> resultadoPVB.txt
sync
echo 3 > /proc/sys/vm/drop_caches
echo -e '\n Caso postgres1-3' >> resultadoPVB.txt
(time sudo -u fuzzy psql fuzzy < postgres1-3.sql) 2>> resultadoPVB.txt
