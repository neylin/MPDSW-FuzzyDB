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

listaArchivos = ['resultadoWVB.txt','resultadoWVB1.txt','resultadoWVA.txt','resultadoWVA1.txt']
listaBdd = ['World']
listaVolumen = ['Bajo','Alto']

for j in range(0, len(listaArchivos)):

	if listaArchivos[j] == 'resultadoWVB.txt' or listaArchivos[j] == 'resultadoWVB1.txt' or listaArchivos[j] == 'resultadoWVA.txt' or listaArchivos[j] == 'resultadoWVA1.txt':
		bdd = listaBdd[0]

	if listaArchivos[j] == 'resultadoWVB.txt' or listaArchivos[j] == 'resultadoWVB1.txt':
		volumen = listaVolumen[0]
	elif listaArchivos[j] == 'resultadoWVA.txt' or listaArchivos[j] == 'resultadoWVA1.txt':
		volumen = listaVolumen[1]

	if os.path.isfile(listaArchivos[j]):

		infilePVB = open(listaArchivos[j], 'r')

		with infilePVB as f:

	    		lines = f.readlines()
		    
		    	for i in range(0, len(lines)):

					if lines[i].strip() == "Caso select2-1": 
					
						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tST1\t" + volumen + "\t" + lineaSeparada
						outfile1.write(lineaWrite)
						
					elif lines[i].strip() == "Caso select2-2":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tST2\t" + volumen + "\t" + lineaSeparada
						outfile1.write(lineaWrite)

					elif lines[i].strip() == "Caso select2-3":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tST3\t" + volumen + "\t" + lineaSeparada
						outfile1.write(lineaWrite)

					elif lines[i].strip() == "Caso select2-4":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tST2T3\t" + volumen + "\t" + lineaSeparada
						outfile1.write(lineaWrite)

					elif lines[i].strip() == "Caso groupby2-1":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tGT1\t" + volumen + "\t" + lineaSeparada
						outfile2.write(lineaWrite)
						
					elif lines[i].strip() == "Caso groupby2-2":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tGT2\t" + volumen + "\t" + lineaSeparada
						outfile2.write(lineaWrite)

					elif lines[i].strip() == "Caso groupby2-3":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tGT3\t" + volumen + "\t" + lineaSeparada
						outfile2.write(lineaWrite)

					elif lines[i].strip() == "Caso groupby2-4":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tGT2T3\t" + volumen + "\t" + lineaSeparada
						outfile2.write(lineaWrite)

					elif lines[i].strip() == "Caso orderby2-1":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tOT1\t" + volumen + "\t" + lineaSeparada
						outfile3.write(lineaWrite)

					elif lines[i].strip() == "Caso orderby2-2":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tOT2\t" + volumen + "\t" + lineaSeparada
						outfile3.write(lineaWrite)

					elif lines[i].strip() == "Caso orderby2-3":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tOT3\t" + volumen + "\t" + lineaSeparada
						outfile3.write(lineaWrite)

					elif lines[i].strip() == "Caso orderby2-4":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tOT2T3\t" + volumen + "\t" + lineaSeparada
						outfile3.write(lineaWrite)

					elif lines[i].strip() == "Caso postgres2-1":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tPS\t" + volumen + "\t" + lineaSeparada
						outfile4.write(lineaWrite)

					elif lines[i].strip() == "Caso postgres2-2":

						lineaArchivo = lines[i+2].strip('real\t')
						lineaSeparada = lineaArchivo.replace('m','').replace('s','');
						lineaWrite =  bdd + "\tPG\t" + volumen + "\t" + lineaSeparada
						outfile4.write(lineaWrite)

					elif lines[i].strip() == "Caso postgres2-3":

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
