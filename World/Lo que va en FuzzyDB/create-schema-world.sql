--Dominio difuso tipo 3
CREATE FUZZY DOMAIN continent AS VALUES ('Europe', 'Oceania', 'Asia', 'North America', 'Africa', 'Antarctica', 'South America') SIMILARITY { ('Europe','Oceania')/0.4, ('Europe','Asia')/0.9, ('Europe','North America')/0.5, ('Europe','Africa')/0.9, ('Europe', 'Antarctica')/0.2, ('Europe','South America')/0.3, ('Oceania','Asia')/0.7, ('Oceania','North America')/0.2, ('Oceania','Africa')/0.4, ('Oceania','Antarctica')/0.8, ('Oceania','South America')/0.4, ('Asia', 'North America')/0.3, ('Asia','Africa')/0.6, ('Asia', 'Antarctica')/0.5, ('Asia','South America')/0.3, ('North America', 'Africa')/0.4, ('North America', 'Antarctica')/0.2, ('North America', 'South America')/0.9, ('Africa', 'Antarctica')/0.3 ,('Africa','South America')/0.3, ('Antarctica', 'South America')/0.6 };

--Dominio difuso tipo 2
CREATE FUZZY DOMAIN poblacion AS POSSIBILITY DISTRIBUTION ON INTEGER;

--Creacion de las tablas
CREATE TABLE city (id integer, name varchar, countrycode varchar, district varchar, population integer, cars integer, poblacion poblacion);

CREATE TABLE country(code varchar, name varchar, continent continent, region varchar, surfacearea real, indepyear smallint, population integer, lifeexpectancy real, gnp numeric, gnpold numeric, localname varchar, governmentform text, headofstate text, capital integer, code2 varchar);

CREATE TABLE countrylanguage (countrycode varchar, language varchar, isofficial boolean, percentage real);

