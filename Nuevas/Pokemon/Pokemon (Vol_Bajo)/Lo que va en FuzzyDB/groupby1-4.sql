SELECT count(*) AS total, poder_base_estimado, color FROM pokemon GROUP BY SIMILAR color, poder_base_estimado;
