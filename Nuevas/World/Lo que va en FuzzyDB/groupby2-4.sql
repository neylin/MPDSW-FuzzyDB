SELECT count(*) AS total, poblacion, continent FROM city, country WHERE countrycode = code GROUP BY SIMILAR continent, poblacion;
