experimento1 <- read.table("experimento1.txt", header = TRUE)
attach(experimento1)
experimento1.aov<-aov(T.Ejecucion~TipoConsulta*VolumenDatos,experimento1)
summary(experimento1.aov)
interaction.plot(response=T.Ejecucion,VolumenDatos,TipoConsulta,xlab="Volumen de Datos", ylab="Media del Tiempo de Ejecución",trace.label="Tipo de Consulta",col=1:4,main="Caso Select-BD Pokemon")
