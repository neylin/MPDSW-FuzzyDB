SELECT count(*) AS total, poblacion FROM city, country WHERE countrycode = code GROUP BY poblacion;
