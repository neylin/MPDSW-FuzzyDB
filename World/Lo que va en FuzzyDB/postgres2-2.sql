SELECT count(*) AS total, district FROM city, country WHERE countrycode = code GROUP BY district;
