experimento4 <- read.table("experimento4.txt", header = TRUE)
attach(experimento4)
experimento4.aov<-aov(T.Ejecucion~TipoConsulta*VolumenDatos,experimento4)
summary(experimento4.aov)
interaction.plot(response=T.Ejecucion,VolumenDatos,TipoConsulta,xlab="Volumen de Datos", ylab="Media del Tiempo de Ejecución",trace.label="Tipo de Consulta",col=1:4,main="Caso Postgres-BD Pokemon")

