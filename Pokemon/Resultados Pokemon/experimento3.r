experimento3 <- read.table("experimento3.txt", header = TRUE)
attach(experimento3)
experimento3.aov<-aov(T.Ejecucion~TipoConsulta*VolumenDatos,experimento3)
summary(experimento3.aov)
interaction.plot(response=T.Ejecucion,VolumenDatos,TipoConsulta,xlab="Volumen de Datos", ylab="Media del Tiempo de Ejecución",trace.label="Tipo de Consulta",col=1:4,main="Caso Order By-BD Pokemon")

