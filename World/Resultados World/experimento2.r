experimento2 <- read.table("experimento2.txt", header = TRUE)
attach(experimento2)
experimento2.aov<-aov(T.Ejecucion~TipoConsulta*VolumenDatos,experimento2)
summary(experimento2.aov)
interaction.plot(response=T.Ejecucion,VolumenDatos,TipoConsulta,xlab="Volumen de Datos", ylab="Media del Tiempo de Ejecución",trace.label="Tipo de Consulta",col=1:4,main="Caso Group By-BD World")

