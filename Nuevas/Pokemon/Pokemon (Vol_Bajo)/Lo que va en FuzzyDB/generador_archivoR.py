# -*- encoding: utf-8 -*-
#!/usr/bin/python2.4

import os

outfile1 = open('experimento1.txt','w')
outfile2 = open('experimento2.txt','w')
outfile3 = open('experimento3.txt','w')
outfile4 = open('experimento4.txt','w')

outfile1.write("BaseDatos\tTipoConsulta\tVolumenDatos\tT.Ejecucion\n")
outfile2.write("BaseDatos\tTipoConsulta\tVolumenDatos\tT.Ejecucion\n")
outfile3.write("BaseDatos\tTipoConsulta\tVolumenDatos\tT.Ejecucion\n")
outfile4.write("BaseDatos\tTipoConsulta\tVolumenDatos\tT.Ejecucion\n")

listaArchivos = ['resultadoPVB.txt','resultadoPVB1.txt','resultadoPVA.txt','resultadoPVA1.txt']
listaBdd = ['Pokemon']
listaVolumen = ['Bajo','Alto']

for j in range(0, len(listaArchivos)):

	if listaArchivos[j] == 'resultadoPVB.txt' or listaArchivos[j] == 'resultadoPVB1.txt' or listaArchivos[j] == 'resultadoPVA.txt' or listaArchivos[j] == 'resultadoPVA1.txt':
		bdd = listaBdd[0]

	if listaArchivos[j] == 'resultadoPVB.txt' or listaArchivos[j] == 'resultadoPVB1.txt':
		volumen = listaVolumen[0]
	elif listaArchivos[j] == 'resultadoPVA.txt' or listaArchivos[j] == 'resultadoPVA1.txt':
		volumen = listaVolumen[1]

	if os.path.isfile(listaArchivos[j]):

		infilePVB = open(listaArchivos[j], 'r')

		with infilePVB as f:

	    		lines = f.readlines()
		    
		    	for i in range(0, len(lines)):

					if lines[i].strip() == "Caso select1-1": 
					
						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tST1\t" + volumen + "\t" + lineaSeparada
						outfile1.write(lineaWrite)
						
					elif lines[i].strip() == "Caso select1-2":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tST2\t" + volumen + "\t" + lineaSeparada
						outfile1.write(lineaWrite)

					elif lines[i].strip() == "Caso select1-3":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tST3\t" + volumen + "\t" + lineaSeparada
						outfile1.write(lineaWrite)

					elif lines[i].strip() == "Caso select1-4":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tST2T3\t" + volumen + "\t" + lineaSeparada
						outfile1.write(lineaWrite)

					elif lines[i].strip() == "Caso groupby1-1":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tGT1\t" + volumen + "\t" + lineaSeparada
						outfile2.write(lineaWrite)
						
					elif lines[i].strip() == "Caso groupby1-2":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tGT2\t" + volumen + "\t" + lineaSeparada
						outfile2.write(lineaWrite)

					elif lines[i].strip() == "Caso groupby1-3":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tGT3\t" + volumen + "\t" + lineaSeparada
						outfile2.write(lineaWrite)

					elif lines[i].strip() == "Caso groupby1-4":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tGT2T3\t" + volumen + "\t" + lineaSeparada
						outfile2.write(lineaWrite)

					elif lines[i].strip() == "Caso orderby1-1":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tOT1\t" + volumen + "\t" + lineaSeparada
						outfile3.write(lineaWrite)

					elif lines[i].strip() == "Caso orderby1-2":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tOT2\t" + volumen + "\t" + lineaSeparada
						outfile3.write(lineaWrite)

					elif lines[i].strip() == "Caso orderby1-3":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tOT3\t" + volumen + "\t" + lineaSeparada
						outfile3.write(lineaWrite)

					elif lines[i].strip() == "Caso orderby1-4":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tOT2T3\t" + volumen + "\t" + lineaSeparada
						outfile3.write(lineaWrite)

					elif lines[i].strip() == "Caso postgres1-1":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tPS\t" + volumen + "\t" + lineaSeparada
						outfile4.write(lineaWrite)

					elif lines[i].strip() == "Caso postgres1-2":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tPG\t" + volumen + "\t" + lineaSeparada
						outfile4.write(lineaWrite)

					elif lines[i].strip() == "Caso postgres1-3":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tPO\t" + volumen + "\t" + lineaSeparada
						outfile4.write(lineaWrite)

# Cerramos el fichero.
					f.close()
	del bdd
	del volumen	

#Cerramos el fichero con las respuestas
outfile1.close()
outfile2.close()
outfile3.close()
outfile4.close()
