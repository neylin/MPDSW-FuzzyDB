SELECT count(*) AS total, continent FROM city, country WHERE countrycode = code GROUP BY SIMILAR continent;
