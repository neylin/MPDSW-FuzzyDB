-- File: centroid-ordering.sql
-- Description: This file contains the stored functions for comparing two
--              fuzzy type II values based on the Riemman integral and the centroid. 

-- Definition of lower element '<'
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_lower(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        mass_moment_1       float := 0.0;
        mass_moment_2       float := 0.0;
        mass_1              float := 0.0;
        mass_2              float := 0.0;
        abscissa_1          float := 0.0;
        abscissa_2          float := 0.0;
        comp1               float := 0;
        comp2               float := 0;
        val                 float := 0;
        size1               int := array_length(elem1.value,1);
        size2               int := array_length(elem2.value,1);
        current_order       int := 0;
        distance            int := 0;
        trapezoid_array_x_1 int[] := '{}';
        trapezoid_array_y_1 float[] := '{}';
        trapezoid_array_x_2 int[] := '{}';
        trapezoid_array_y_2 float[] := '{}';
        b                   float := 0;
        pendiente           float := 0;
        index               int := 1;
    BEGIN
        SELECT ordering INTO current_order FROM information_schema_fuzzy.current_orderings2;

        IF current_order = 1 THEN
            -- Both numbers are expressed by extension
            IF ((elem1.type = FALSE) AND (elem1.value[2] IS NOT NULL) AND (elem1.value[3] IS NOT NULL)) THEN
                distance := elem1.value[2] - elem1.value[1] -1;
                pendiente := 1::float / (elem1.value[2] - elem1.value[1]);
                b := -(pendiente*elem1.value[1]);

                for i in (elem1.value[1] + 1)..(elem1.value[1] + distance) loop
                    trapezoid_array_y_1[index] = (pendiente * i) + b;
                    trapezoid_array_x_1[index] = i;
                    index := index + 1;
                end loop;

                trapezoid_array_x_1[index] = elem1.value[2];
                trapezoid_array_y_1[index] = 1;
                index := index + 1;

                distance := elem1.value[3] - elem1.value[2] -1;
                for i in 1..distance loop
                    trapezoid_array_x_1[index] = elem1.value[2] + i;
                    trapezoid_array_y_1[index] = 1;
                    index := index + 1;
                end loop;

                trapezoid_array_x_1[index] = elem1.value[3];
                trapezoid_array_y_1[index] = 1;
                index := index + 1;

                distance := elem1.value[4] - elem1.value[3] -1;
                pendiente := (-1)::float / (elem1.value[4] - elem1.value[3]);
                b := -(pendiente*elem1.value[4]);
                for i in (elem1.value[3] + 1)..(elem1.value[3] + distance) loop
                    trapezoid_array_x_1[index] = i;
                    trapezoid_array_y_1[index] = (pendiente * i) + b;
                    index := index + 1;
                end loop;
            END IF;

            IF ((elem2.type = FALSE) AND (elem2.value[2] IS NOT NULL) AND (elem2.value[3] IS NOT NULL)) THEN
                index := 1;
                distance := elem2.value[2] - elem2.value[1] -1;
                pendiente := 1::float / (elem2.value[2] - elem2.value[1]);
                b := -(pendiente*elem2.value[1]);

                for i in (elem2.value[1] + 1)..(elem2.value[1] + distance) loop
                    trapezoid_array_y_2[index] = (pendiente * i) + b;
                    trapezoid_array_x_2[index] = i;
                    index := index + 1;
                end loop;

                trapezoid_array_x_2[index] = elem2.value[2];
                trapezoid_array_y_2[index] = 1;
                index := index + 1;

                distance := elem2.value[3] - elem2.value[2] -1;
                for i in 1..distance loop
                    trapezoid_array_x_2[index] = elem2.value[2] + i;
                    trapezoid_array_y_2[index] = 1;
                    index := index + 1;
                end loop;

                trapezoid_array_x_2[index] = elem2.value[3];
                trapezoid_array_y_2[index] = 1;
                index := index + 1;

                distance := elem2.value[4] - elem2.value[3] -1;
                pendiente := (-1)::float / (elem2.value[4] - elem2.value[3]);
                b := -(pendiente*elem2.value[4]);
                for i in (elem2.value[3] + 1)..(elem2.value[3] + distance) loop
                    trapezoid_array_x_2[index] = i;
                    trapezoid_array_y_2[index] = (pendiente * i) + b;
                    index := index + 1;
                end loop;
            END IF;

            IF elem1.type and elem2.type THEN
                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > elem1.value[j] THEN
                            comp1 := comp1 + (elem2.odd[i] * elem1.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                size1 = size2;
                size2 = array_length(elem1.value,1);

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem1.value[i] > elem2.value[j] THEN
                            comp2 := comp2 + (elem1.odd[i] * elem2.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp1 > comp2;

            -- Both numbers are expressed by trapezoids
            ELSE IF (elem1.type = False) and (elem2.type = False) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;

                FOR j IN 1..array_length(trapezoid_array_x_1,1) LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_2,1) LOOP
                        IF trapezoid_array_x_2[i] > trapezoid_array_x_1[j] THEN
                            comp1 := comp1 + (trapezoid_array_y_2[i] * trapezoid_array_y_1[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..array_length(trapezoid_array_x_2,1) LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_1,1) LOOP
                        IF trapezoid_array_x_1[i] > trapezoid_array_x_2[j] THEN
                            comp2 := comp2 + (trapezoid_array_y_1[i] * trapezoid_array_y_2[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp1 > comp2;
            END IF;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = False) and (elem2.type = True) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;

                FOR j IN 1..array_length(trapezoid_array_x_1,1) LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > trapezoid_array_x_1[j] THEN
                            comp1 := comp1 + (elem2.odd[i] * trapezoid_array_y_1[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..size2 LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_1,1) LOOP
                        IF trapezoid_array_x_1[i] > elem2.value[j] THEN
                            comp2 := comp2 + (trapezoid_array_y_1[i] * elem2.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp1 > comp2;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = True) and (elem2.type = False) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_2,1) LOOP
                        IF trapezoid_array_x_2[i] > elem1.value[j] THEN
                            comp1 := comp1 + (trapezoid_array_y_2[i] * elem1.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..array_length(trapezoid_array_x_2,1) LOOP
                    FOR i IN 1..size1 LOOP
                        IF elem1.value[i] > trapezoid_array_x_2[j] THEN
                            comp2 := comp2 + (elem1.odd[i] * trapezoid_array_y_2[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp1 > comp2;
            END IF;
        END IF;

        IF current_order = 2 THEN
            -- Both numbers are expressed by extension
            IF elem1.type and elem2.type THEN
                -- Traverse elem1 to calculate its mass moment and its mass
                FOR j IN 1..size1 LOOP
                    mass_moment_1   := mass_moment_1 + (elem1.value[j] * elem1.odd[j]);
                    mass_1          := mass_1 + elem1.odd[j];
                END LOOP;
                -- Traverse elem2 to calculate its mass moment and its mass
                FOR i IN 1..size2 LOOP
                    mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                    mass_2          := mass_2 + elem2.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_1 := mass_moment_1::float / mass_1;
                abscissa_2 := mass_moment_2::float / mass_2;
                RETURN abscissa_2 > abscissa_1;

            -- Both numbers are expressed by trapezoids
            ELSE IF (elem1.type = FALSE) AND (elem2.type = FALSE) THEN
                -- elem1 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;   
                -- Calculte both the abscissas
                abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
                abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
                abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

                abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
                abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
                abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_2);

                RETURN abscissa_2 > abscissa_1;
            END IF;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = FALSE) AND (elem2.type = TRUE) THEN
                    -- elem1 is a right shoulder trapezoid type
                    IF (elem1.value[2] IS NULL) THEN
                        RETURN TRUE;
                    END IF;
                    -- Traverse elem2 to calculate its mass moment and its mass
                    FOR i IN 1..size2 LOOP
                        mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                        mass_2          := mass_2 + elem2.odd[i];
                    END LOOP;
                    -- Calculte both the abscissas
                    abscissa_2 := mass_moment_2::float / mass_2;

                    abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
                    abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
                    abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

                    RETURN abscissa_2 > abscissa_1;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = TRUE) AND (elem2.type = FALSE) THEN
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- Traverse elem1 to calculate its mass moment and its mass
                FOR i IN 1..size1 LOOP
                    mass_moment_1   := mass_moment_1 + (elem1.value[i] * elem1.odd[i]);
                    mass_1          := mass_1 + elem1.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_1 := mass_moment_1::float / mass_1;

                abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
                abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
                abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_1);

                RETURN abscissa_2 > abscissa_1;
            END IF;
        END IF;

        IF current_order = 3 THEN
            -- Both numbers are expressed by extension
            IF elem1.type and elem2.type THEN
                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > elem1.value[j] THEN
                            val := 0;

                            -- Obtain the minimum value between the two elements
                            IF elem2.odd[i] > elem1.odd[j] THEN
                                val := elem1.odd[j];
                            ELSE 
                                val := elem2.odd[i];
                            END IF;

                            -- Obtain the maximum value between all the elements
                            IF val > comp1 THEN
                                comp1 := val;
                            END IF;

                            EXIT WHEN comp1 = 1;
                        END IF;
                    END LOOP;
                    EXIT WHEN comp1 = 1;
                END LOOP;

                size1 = size2;
                size2 = array_length(elem1.value,1);

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem1.value[i] > elem2.value[j] THEN
                            val := 0;

                            IF elem1.odd[i] > elem2.odd[j] THEN
                                val := elem2.odd[j];
                            ELSE 
                                val := elem1.odd[i];
                            END IF;

                            IF val > comp2 THEN
                                comp2 := val;
                            END IF;

                            EXIT WHEN comp2 = 1;
                        END IF;
                    END LOOP;

                    EXIT WHEN comp2 = 1;
                END LOOP;

                return comp1 > comp2;

            ELSE IF (elem1.type = False) and (elem2.type = False) THEN
                IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
                    IF (elem1.value[2] is not Null) THEN
                        IF elem1.value[2] > elem2.value[3] THEN
                            return False;
                        END IF;

                        return False;			
                    END IF;
                    return False;
                END IF;

                IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
                    IF elem1.value[3] < elem2.value[2] THEN
                        return True;		
                    END IF;

                    IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
                        return False;
                    END IF;

                    IF elem1.value[2] > elem2.value[3]  THEN
                        return False;
                    END IF;

                    return False;
                END IF;	

                IF (elem1.value[3] is Null) THEN
                    IF (elem2.value[3] is null) THEN
                        return False;		
                    END IF;

                    IF elem1.value[2] > elem2.value[3] THEN
                        return False;
                    ELSE
                        return False;
                    END IF;	
                END IF;
            END IF;
            END IF;

            IF (elem1.type = False) and (elem2.type = True) THEN
                    IF elem1.value[2] is Null THEN
                            return True;
                    END IF;
                    FOR i IN 1..size2 LOOP
                            IF elem2.odd[i] = 1 THEN
                                    IF elem1.value[2] is not Null THEN
                                            IF elem1.value[2] <= elem2.value[i] THEN
                                                    return True;
                                            END IF;
                                    END IF;
                            END IF;
                    END LOOP;
                    return False;
            END IF;

            IF (elem1.type = True) and (elem2.type = False) THEN
                    IF elem2.value[3] is Null THEN
                            return True;
                    END IF;
                    FOR i IN 1..size1 LOOP
                            IF elem1.odd[i] = 1 THEN
                                    IF elem2.value[2] is not Null THEN
                                            IF elem1.value[i] <= elem2.value[3] THEN
                                                    return True;
                                            END IF;
                                    END IF;
                            END IF;
                    END LOOP;
                    return False;
            END IF;
        END IF;

    END;
$$ LANGUAGE plpgsql;

-- Definition of lower or equal element '<='
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_lower_eq(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        mass_moment_1   float := 0.0;
        mass_moment_2   float := 0.0;
        mass_1          float := 0.0;
        mass_2          float := 0.0;
        abscissa_1      float := 0.0;
        abscissa_2      float := 0.0;
        comp1           float := 0;
        comp2           float := 0;
        val             float := 0;
        size1           int := array_length(elem1.value,1);
        size2           int := array_length(elem2.value,1);
        current_order   int := 0;
        distance            int := 0;
        trapezoid_array_x_1 int[] := '{}';
        trapezoid_array_y_1 float[] := '{}';
        trapezoid_array_x_2 int[] := '{}';
        trapezoid_array_y_2 float[] := '{}';
        b                   float := 0;
        pendiente           float := 0;
        index               int := 1;
    BEGIN
        SELECT ordering INTO current_order FROM information_schema_fuzzy.current_orderings2;

        IF current_order = 1 THEN

            IF ((elem1.type = FALSE) AND (elem1.value[2] IS NOT NULL) AND (elem1.value[3] IS NOT NULL)) THEN
                distance := elem1.value[2] - elem1.value[1] -1;
                pendiente := 1::float / (elem1.value[2] - elem1.value[1]);
                b := -(pendiente*elem1.value[1]);

                for i in (elem1.value[1] + 1)..(elem1.value[1] + distance) loop
                    trapezoid_array_y_1[index] = (pendiente * i) + b;
                    trapezoid_array_x_1[index] = i;
                    index := index + 1;
                end loop;

                trapezoid_array_x_1[index] = elem1.value[2];
                trapezoid_array_y_1[index] = 1;
                index := index + 1;

                distance := elem1.value[3] - elem1.value[2] -1;
                for i in 1..distance loop
                    trapezoid_array_x_1[index] = elem1.value[2] + i;
                    trapezoid_array_y_1[index] = 1;
                    index := index + 1;
                end loop;

                trapezoid_array_x_1[index] = elem1.value[3];
                trapezoid_array_y_1[index] = 1;
                index := index + 1;

                distance := elem1.value[4] - elem1.value[3] -1;
                pendiente := (-1)::float / (elem1.value[4] - elem1.value[3]);
                b := -(pendiente*elem1.value[4]);
                for i in (elem1.value[3] + 1)..(elem1.value[3] + distance) loop
                    trapezoid_array_x_1[index] = i;
                    trapezoid_array_y_1[index] = (pendiente * i) + b;
                    index := index + 1;
                end loop;
            END IF;

            IF ((elem2.type = FALSE) AND (elem2.value[2] IS NOT NULL) AND (elem2.value[3] IS NOT NULL)) THEN
                index := 1;
                distance := elem2.value[2] - elem2.value[1] -1;
                pendiente := 1::float / (elem2.value[2] - elem2.value[1]);
                b := -(pendiente*elem2.value[1]);

                for i in (elem2.value[1] + 1)..(elem2.value[1] + distance) loop
                    trapezoid_array_y_2[index] = (pendiente * i) + b;
                    trapezoid_array_x_2[index] = i;
                    index := index + 1;
                end loop;

                trapezoid_array_x_2[index] = elem2.value[2];
                trapezoid_array_y_2[index] = 1;
                index := index + 1;

                distance := elem2.value[3] - elem2.value[2] -1;
                for i in 1..distance loop
                    trapezoid_array_x_2[index] = elem2.value[2] + i;
                    trapezoid_array_y_2[index] = 1;
                    index := index + 1;
                end loop;

                trapezoid_array_x_2[index] = elem2.value[3];
                trapezoid_array_y_2[index] = 1;
                index := index + 1;

                distance := elem2.value[4] - elem2.value[3] -1;
                pendiente := (-1)::float / (elem2.value[4] - elem2.value[3]);
                b := -(pendiente*elem2.value[4]);
                for i in (elem2.value[3] + 1)..(elem2.value[3] + distance) loop
                    trapezoid_array_x_2[index] = i;
                    trapezoid_array_y_2[index] = (pendiente * i) + b;
                    index := index + 1;
                end loop;
            END IF;

            IF elem1.type and elem2.type THEN
                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > elem1.value[j] THEN
                            comp1 := comp1 + (elem2.odd[i] * elem1.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                size1 = size2;
                size2 = array_length(elem1.value,1);

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem1.value[i] > elem2.value[j] THEN
                            comp2 := comp2 + (elem1.odd[i] * elem2.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp1 >= comp2;

            ELSE IF (elem1.type = False) and (elem2.type = False) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;

                FOR j IN 1..array_length(trapezoid_array_x_1,1) LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_2,1) LOOP
                        IF trapezoid_array_x_2[i] > trapezoid_array_x_1[j] THEN
                            comp1 := comp1 + (trapezoid_array_y_2[i] * trapezoid_array_y_1[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..array_length(trapezoid_array_x_2,1) LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_1,1) LOOP
                        IF trapezoid_array_x_1[i] > trapezoid_array_x_2[j] THEN
                            comp2 := comp2 + (trapezoid_array_y_1[i] * trapezoid_array_y_2[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp1 >= comp2;
            END IF;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = False) and (elem2.type = True) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;

                FOR j IN 1..array_length(trapezoid_array_x_1,1) LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > trapezoid_array_x_1[j] THEN
                            comp1 := comp1 + (elem2.odd[i] * trapezoid_array_y_1[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..size2 LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_1,1) LOOP
                        IF trapezoid_array_x_1[i] > elem2.value[j] THEN
                            comp2 := comp2 + (trapezoid_array_y_1[i] * elem2.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp1 >= comp2;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = True) and (elem2.type = False) THEN
                IF (elem2.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_2,1) LOOP
                        IF trapezoid_array_x_2[i] > elem1.value[j] THEN
                            comp1 := comp1 + (trapezoid_array_y_2[i] * elem1.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..array_length(trapezoid_array_x_2,1) LOOP
                    FOR i IN 1..size1 LOOP
                        IF elem1.value[i] > trapezoid_array_x_2[j] THEN
                            comp2 := comp2 + (elem1.odd[i] * trapezoid_array_y_2[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp1 >= comp2;
            END IF;
        END IF;    

        IF current_order = 2 THEN
            -- Both numbers are expressed by extension
            IF elem1.type and elem2.type THEN
                -- Traverse elem1 to calculate its mass moment and its mass
                FOR j IN 1..size1 LOOP
                    mass_moment_1   := mass_moment_1 + (elem1.value[j] * elem1.odd[j]);
                    mass_1          := mass_1 + elem1.odd[j];
                END LOOP;
                -- Traverse elem2 to calculate its mass moment and its mass
                FOR i IN 1..size2 LOOP
                    mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                    mass_2          := mass_2 + elem2.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_1 := mass_moment_1::float / mass_1;
                abscissa_2 := mass_moment_2::float / mass_2;
                RETURN abscissa_2 >= abscissa_1;

            -- Both numbers are expressed by trapezoids
            ELSE IF (elem1.type = FALSE) AND (elem2.type = FALSE) THEN
                -- elem1 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem1 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- Calculte both the abscissas
                abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
                abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
                abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

                abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
                abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
                abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_2);

                RETURN abscissa_2 >= abscissa_1;
            END IF;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = FALSE) AND (elem2.type = TRUE) THEN
                -- elem1 is a left shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- Traverse elem2 to calculate its mass moment and its mass
                FOR i IN 1..size2 LOOP
                    mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                    mass_2          := mass_2 + elem2.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_2 := mass_moment_2::float / mass_2;

                abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
                abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
                abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

                RETURN abscissa_2 >= abscissa_1;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = TRUE) AND (elem2.type = FALSE) THEN
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- Traverse elem1 to calculate its mass moment and its mass
                FOR i IN 1..size1 LOOP
                    mass_moment_1   := mass_moment_1 + (elem1.value[i] * elem1.odd[i]);
                    mass_1          := mass_1 + elem1.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_1 := mass_moment_1::float / mass_1;

                abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
                abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
                abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_1);

                RETURN abscissa_2 >= abscissa_1;
            END IF;
        END IF;

        IF current_order = 3 THEN
            IF elem1.type and elem2.type THEN
                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > elem1.value[j] THEN
                            val := 0;

                            IF elem2.odd[i] > elem1.odd[j] THEN
                                val := elem1.odd[j];
                            ELSE 
                                val := elem2.odd[i];
                            END IF;

                            IF val > comp1 THEN
                                comp1 := val;
                            END IF;

                            EXIT WHEN comp1 = 1;
                        END IF;
                    END LOOP;

                    EXIT WHEN comp1 = 1;
                END LOOP;

                size1 = size2;
                size2 = array_length(elem1.value,1);

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem1.value[i] > elem2.value[j] THEN
                            val := 0;

                            IF elem1.odd[i] > elem2.odd[j] THEN
                                val := elem2.odd[j];
                            ELSE 
                                val := elem1.odd[i];
                            END IF;

                            IF val > comp2 THEN
                                comp2 := val;
                            END IF;

                            EXIT WHEN comp2 = 1;
                        END IF;
                    END LOOP;

                    EXIT WHEN comp2 = 1;
                END LOOP;

                return comp1 >= comp2;

            ELSE IF (elem1.type = False) and (elem2.type = False) THEN
                IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
                    IF (elem1.value[2] is not Null) THEN
                        IF elem1.value[2] > elem2.value[3] THEN
                            return False;
                        END IF;

                        return True;			
                    END IF;
                    return True;
                END IF;

                IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
                    IF elem1.value[3] <= elem2.value[2] THEN
                        return True;		
                    END IF;

                    IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
                        return True;
                    END IF;

                    IF elem1.value[2] > elem2.value[3]  THEN
                        return False;
                    END IF;

                    return True;
                END IF;	

                IF (elem1.value[3] is Null) THEN
                    IF (elem2.value[3] is null) THEN
                        return True;		
                    END IF;

                    IF elem1.value[2] > elem2.value[3] THEN
                        return False;
                    ELSE
                        return True;
                    END IF;	
                END IF;
            END IF;
            END IF;

            IF (elem1.type = False) and (elem2.type = True) THEN
                IF elem1.value[2] is Null THEN
                    return True;
                END IF;

                FOR i IN 1..size2 LOOP
                    IF elem2.odd[i] = 1 THEN
                        IF elem1.value[2] is not Null THEN
                            IF elem1.value[2] <= elem2.value[i] THEN
                                return True;
                            END IF;
                        END IF;
                    END IF;
                END LOOP;

                return False;
            END IF;

            IF (elem1.type = True) and (elem2.type = False) THEN
                IF elem2.value[3] is Null THEN
                    return True;
                END IF;

                FOR i IN 1..size1 LOOP
                    IF elem1.odd[i] = 1 THEN
                        IF elem2.value[2] is not Null THEN
                            IF elem1.value[i] <= elem2.value[3] THEN
                                return True;
                            END IF;
                        END IF;
                    END IF;
                END LOOP;

                return False;
            END IF;
        END IF;
    END;
$$ LANGUAGE plpgsql;

-- Definition of equal element '='
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_eq(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        mass_moment_1   float := 0.0;
        mass_moment_2   float := 0.0;
        mass_1          float := 0.0;
        mass_2          float := 0.0;
        abscissa_1      float := 0.0;
        abscissa_2      float := 0.0;
        comp1           float := 0;
        comp2           float := 0;
        val             float := 0;
        bool1           boolean;
        bool2           boolean;
        size1           int := array_length(elem1.value,1);
        size2           int := array_length(elem2.value,1);
        current_order   int := 0;
        distance            int := 0;
        trapezoid_array_x_1 int[] := '{}';
        trapezoid_array_y_1 float[] := '{}';
        trapezoid_array_x_2 int[] := '{}';
        trapezoid_array_y_2 float[] := '{}';
        b                   float := 0;
        pendiente           float := 0;
        index               int := 1;
    BEGIN
        SELECT ordering INTO current_order FROM information_schema_fuzzy.current_orderings2;

        IF current_order = 1 THEN

            IF ((elem1.type = FALSE) AND (elem1.value[2] IS NOT NULL) AND (elem1.value[3] IS NOT NULL)) THEN
                distance := elem1.value[2] - elem1.value[1] -1;
                pendiente := 1::float / (elem1.value[2] - elem1.value[1]);
                b := -(pendiente*elem1.value[1]);

                for i in (elem1.value[1] + 1)..(elem1.value[1] + distance) loop
                    trapezoid_array_y_1[index] = (pendiente * i) + b;
                    trapezoid_array_x_1[index] = i;
                    index := index + 1;
                end loop;

                trapezoid_array_x_1[index] = elem1.value[2];
                trapezoid_array_y_1[index] = 1;
                index := index + 1;

                distance := elem1.value[3] - elem1.value[2] -1;
                for i in 1..distance loop
                    trapezoid_array_x_1[index] = elem1.value[2] + i;
                    trapezoid_array_y_1[index] = 1;
                    index := index + 1;
                end loop;

                trapezoid_array_x_1[index] = elem1.value[3];
                trapezoid_array_y_1[index] = 1;
                index := index + 1;

                distance := elem1.value[4] - elem1.value[3] -1;
                pendiente := (-1)::float / (elem1.value[4] - elem1.value[3]);
                b := -(pendiente*elem1.value[4]);
                for i in (elem1.value[3] + 1)..(elem1.value[3] + distance) loop
                    trapezoid_array_x_1[index] = i;
                    trapezoid_array_y_1[index] = (pendiente * i) + b;
                    index := index + 1;
                end loop;
            END IF;

            IF ((elem2.type = FALSE) AND (elem2.value[2] IS NOT NULL) AND (elem2.value[3] IS NOT NULL)) THEN
                index := 1;
                distance := elem2.value[2] - elem2.value[1] -1;
                pendiente := 1::float / (elem2.value[2] - elem2.value[1]);
                b := -(pendiente*elem2.value[1]);

                for i in (elem2.value[1] + 1)..(elem2.value[1] + distance) loop
                    trapezoid_array_y_2[index] = (pendiente * i) + b;
                    trapezoid_array_x_2[index] = i;
                    index := index + 1;
                end loop;

                trapezoid_array_x_2[index] = elem2.value[2];
                trapezoid_array_y_2[index] = 1;
                index := index + 1;

                distance := elem2.value[3] - elem2.value[2] -1;
                for i in 1..distance loop
                    trapezoid_array_x_2[index] = elem2.value[2] + i;
                    trapezoid_array_y_2[index] = 1;
                    index := index + 1;
                end loop;

                trapezoid_array_x_2[index] = elem2.value[3];
                trapezoid_array_y_2[index] = 1;
                index := index + 1;

                distance := elem2.value[4] - elem2.value[3] -1;
                pendiente := (-1)::float / (elem2.value[4] - elem2.value[3]);
                b := -(pendiente*elem2.value[4]);
                for i in (elem2.value[3] + 1)..(elem2.value[3] + distance) loop
                    trapezoid_array_x_2[index] = i;
                    trapezoid_array_y_2[index] = (pendiente * i) + b;
                    index := index + 1;
                end loop;
            END IF;

            IF elem1.type and elem2.type THEN
                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > elem1.value[j] THEN

                            comp1 := comp1 + (elem2.odd[i] * elem1.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                size1 = size2;
                size2 = array_length(elem1.value,1);

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem1.value[i] > elem2.value[j] THEN
                            comp2 := comp2 + (elem1.odd[i] * elem2.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;
                return comp2 = comp1;

            -- Both numbers are expressed by trapezoids
            ELSE IF (elem1.type = False) and (elem2.type = False) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;

                FOR j IN 1..array_length(trapezoid_array_x_1,1) LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_2,1) LOOP
                        IF trapezoid_array_x_2[i] > trapezoid_array_x_1[j] THEN
                            comp1 := comp1 + (trapezoid_array_y_2[i] * trapezoid_array_y_1[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..array_length(trapezoid_array_x_2,1) LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_1,1) LOOP
                        IF trapezoid_array_x_1[i] > trapezoid_array_x_2[j] THEN
                            comp2 := comp2 + (trapezoid_array_y_1[i] * trapezoid_array_y_2[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp1 = comp2;
            END IF;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = False) and (elem2.type = True) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;

                FOR j IN 1..array_length(trapezoid_array_x_1,1) LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > trapezoid_array_x_1[j] THEN
                            comp1 := comp1 + (elem2.odd[i] * trapezoid_array_y_1[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..size2 LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_1,1) LOOP
                        IF trapezoid_array_x_1[i] > elem2.value[j] THEN
                            comp2 := comp2 + (trapezoid_array_y_1[i] * elem2.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp1 = comp2;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = True) and (elem2.type = False) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_2,1) LOOP
                        IF trapezoid_array_x_2[i] > elem1.value[j] THEN
                            comp1 := comp1 + (trapezoid_array_y_2[i] * elem1.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..array_length(trapezoid_array_x_2,1) LOOP
                    FOR i IN 1..size1 LOOP
                        IF elem1.value[i] > trapezoid_array_x_2[j] THEN
                            comp2 := comp2 + (elem1.odd[i] * trapezoid_array_y_2[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp1 = comp2;
            END IF;
        END IF;

        IF current_order = 2 THEN
            -- Both numbers are expressed by extension
            IF elem1.type and elem2.type THEN
                -- Traverse elem1 to calculate its mass moment and its mass
                FOR j IN 1..size1 LOOP
                    mass_moment_1   := mass_moment_1 + (elem1.value[j] * elem1.odd[j]);
                    mass_1          := mass_1 + elem1.odd[j];
                END LOOP;
                -- Traverse elem2 to calculate its mass moment and its mass
                FOR i IN 1..size2 LOOP
                    mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                    mass_2          := mass_2 + elem2.odd[i];
                END LOOP;
                -- Calculate both the abscissas
                abscissa_1 := mass_moment_1::float / mass_1;
                abscissa_2 := mass_moment_2::float / mass_2;
                RETURN abscissa_1 = abscissa_2;

            -- Both numbers are expressed by trapezoids
            ELSE IF (elem1.type = FALSE) AND (elem2.type = FALSE) THEN
                -- elem1 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem1 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- Calculte both the abscissas
                abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
                abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
                abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

                abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
                abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
                abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_2);

                RETURN abscissa_1 = abscissa_2;
            END IF;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = FALSE) AND (elem2.type = TRUE) THEN
                -- elem1 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- Traverse elem2 to calculate its mass moment and its mass
                FOR i IN 1..size2 LOOP
                    mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                    mass_2          := mass_2 + elem2.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_2 := mass_moment_2::float / mass_2;

                abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
                abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
                abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

                RETURN abscissa_1::float = abscissa_2;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = TRUE) AND (elem2.type = FALSE) THEN
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- Traverse elem1 to calculate its mass moment and its mass
                FOR i IN 1..size1 LOOP
                    mass_moment_1   := mass_moment_1 + (elem1.value[i] * elem1.odd[i]);
                    mass_1          := mass_1 + elem1.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_1 := mass_moment_1::float / mass_1;

                abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
                abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
                abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_1);

                RETURN abscissa_1 = abscissa_2;
            END IF;
        END IF;

        IF current_order = 3 THEN
            IF elem1.type and elem2.type THEN
                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > elem1.value[j] THEN
                            val := 0;

                            IF elem2.odd[i] > elem1.odd[j] THEN
                                val := elem1.odd[j];
                            ELSE 
                                val := elem2.odd[i];
                            END IF;

                            IF val > comp1 THEN
                                comp1 := val;
                            END IF;

                            EXIT WHEN comp1 = 1;
                        END IF;
                    END LOOP;

                    EXIT WHEN comp1 = 1;
                END LOOP;

                size1 = size2;
                size2 = array_length(elem1.value,1);

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem1.value[i] > elem2.value[j] THEN
                            val := 0;

                            IF elem1.odd[i] > elem2.odd[j] THEN
                                val := elem2.odd[j];
                            ELSE 
                                val := elem1.odd[i];
                            END IF;

                            IF val > comp2 THEN
                                comp2 := val;
                            END IF;

                            EXIT WHEN comp2 = 1;
                        END IF;
                    END LOOP;

                    EXIT WHEN comp2 = 1;
                END LOOP;

                return comp2 = comp1;

            ELSE IF (elem1.type = False) and (elem2.type = False) THEN
                IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
                    IF (elem1.value[2] is not Null) THEN
                        IF elem1.value[2] > elem2.value[3] THEN
                            return False;
                        END IF;

                        return True;			
                    END IF;
                END IF;

                IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
                    IF elem1.value[3] < elem2.value[2] THEN
                        return False;		
                    END IF;

                    IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
                        return True;
                    END IF;

                    IF elem1.value[2] > elem2.value[3]  THEN
                        return False;
                    END IF;

                    return True;
                END IF;	

                IF (elem1.value[3] is Null) THEN
                    IF (elem2.value[3] is null) THEN
                        return True;		
                    END IF;

                    IF elem1.value[2] > elem2.value[3] THEN
                        return False;
                    ELSE
                        return True;
                    END IF;	
                END IF;
            END IF;
            END IF;

            IF (elem1.type = False) and (elem2.type = True) THEN
                bool1 := False;
                bool2 := False;

                IF elem1.value[2] is Null THEN
                    bool1 := True;
                END IF;

                IF elem1.value[3] is Null THEN
                    bool2 := True;
                END IF;

                FOR i IN 1..size2 LOOP
                    IF elem2.odd[i] = 1 THEN
                        IF elem1.value[2] is not Null THEN
                            IF elem1.value[2] <= elem2.value[i] THEN
                                bool1 := True;
                            END IF;
                        END IF;

                        IF elem1.value[3] is not Null THEN
                            IF elem1.value[3] >= elem2.value[i] THEN
                                bool2 := True;
                            END IF;
                        END IF;
                    END IF;

                    IF bool1 and bool2 THEN
                        return True;
                    END IF;
                END LOOP;

                return False;
            END IF;

            IF (elem1.type = True) and (elem2.type = False) THEN
                bool1 := False;
                bool2 := False;

                IF elem2.value[2] is Null THEN
                    bool1 := True;
                END IF;

                IF elem2.value[3] is Null THEN
                    bool2 := True;
                END IF;

                FOR i IN 1..size1 LOOP
                    IF elem1.odd[i] = 1 THEN
                        IF elem2.value[2] is not Null THEN
                            IF elem2.value[2] <= elem1.value[i] THEN
                                bool1 := True;
                            END IF;
                        END IF;

                        IF elem2.value[3] is not Null THEN
                            IF elem2.value[3] >= elem1.value[i] THEN
                                bool2 := True;
                            END IF;
                        END IF;
                    END IF;

                    IF bool1 and bool2 THEN
                        return True;
                    END IF;
                END LOOP;

                return False;
            END IF;
        END IF;

    END;
$$ LANGUAGE plpgsql;

-- Definition of greater element '>'
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_greater(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        mass_moment_1   float := 0.0;
        mass_moment_2   float := 0.0;
        mass_1          float := 0.0;
        mass_2          float := 0.0;
        abscissa_1      float := 0.0;
        abscissa_2      float := 0.0;
        comp1           float := 0;
        comp2           float := 0;
        val             float := 0;
        size1           int := array_length(elem1.value,1);
        size2           int := array_length(elem2.value,1);
        current_order   int := 0;
        distance            int := 0;
        trapezoid_array_x_1 int[] := '{}';
        trapezoid_array_y_1 float[] := '{}';
        trapezoid_array_x_2 int[] := '{}';
        trapezoid_array_y_2 float[] := '{}';
        b                   float := 0;
        pendiente           float := 0;
        index               int := 1;
    BEGIN
        SELECT ordering INTO current_order FROM information_schema_fuzzy.current_orderings2;

        IF current_order = 1 THEN
            IF ((elem1.type = FALSE) AND (elem1.value[2] IS NOT NULL) AND (elem1.value[3] IS NOT NULL)) THEN
                distance := elem1.value[2] - elem1.value[1] -1;
                pendiente := 1::float / (elem1.value[2] - elem1.value[1]);
                b := -(pendiente*elem1.value[1]);

                for i in (elem1.value[1] + 1)..(elem1.value[1] + distance) loop
                    trapezoid_array_y_1[index] = (pendiente * i) + b;
                    trapezoid_array_x_1[index] = i;
                    index := index + 1;
                end loop;

                trapezoid_array_x_1[index] = elem1.value[2];
                trapezoid_array_y_1[index] = 1;
                index := index + 1;

                distance := elem1.value[3] - elem1.value[2] -1;
                for i in 1..distance loop
                    trapezoid_array_x_1[index] = elem1.value[2] + i;
                    trapezoid_array_y_1[index] = 1;
                    index := index + 1;
                end loop;

                trapezoid_array_x_1[index] = elem1.value[3];
                trapezoid_array_y_1[index] = 1;
                index := index + 1;

                distance := elem1.value[4] - elem1.value[3] -1;
                pendiente := (-1)::float / (elem1.value[4] - elem1.value[3]);
                b := -(pendiente*elem1.value[4]);
                for i in (elem1.value[3] + 1)..(elem1.value[3] + distance) loop
                    trapezoid_array_x_1[index] = i;
                    trapezoid_array_y_1[index] = (pendiente * i) + b;
                    index := index + 1;
                end loop;
            END IF;

            IF ((elem2.type = FALSE) AND (elem2.value[2] IS NOT NULL) AND (elem2.value[3] IS NOT NULL)) THEN
                index := 1;
                distance := elem2.value[2] - elem2.value[1] -1;
                pendiente := 1::float / (elem2.value[2] - elem2.value[1]);
                b := -(pendiente*elem2.value[1]);

                for i in (elem2.value[1] + 1)..(elem2.value[1] + distance) loop
                    trapezoid_array_y_2[index] = (pendiente * i) + b;
                    trapezoid_array_x_2[index] = i;
                    index := index + 1;
                end loop;

                trapezoid_array_x_2[index] = elem2.value[2];
                trapezoid_array_y_2[index] = 1;
                index := index + 1;

                distance := elem2.value[3] - elem2.value[2] -1;
                for i in 1..distance loop
                    trapezoid_array_x_2[index] = elem2.value[2] + i;
                    trapezoid_array_y_2[index] = 1;
                    index := index + 1;
                end loop;

                trapezoid_array_x_2[index] = elem2.value[3];
                trapezoid_array_y_2[index] = 1;
                index := index + 1;

                distance := elem2.value[4] - elem2.value[3] -1;
                pendiente := (-1)::float / (elem2.value[4] - elem2.value[3]);
                b := -(pendiente*elem2.value[4]);
                for i in (elem2.value[3] + 1)..(elem2.value[3] + distance) loop
                    trapezoid_array_x_2[index] = i;
                    trapezoid_array_y_2[index] = (pendiente * i) + b;
                    index := index + 1;
                end loop;
            END IF;

            -- Both numbers are expressed by extension
            IF elem1.type and elem2.type THEN
                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > elem1.value[j] THEN
                            comp1 := comp1 + (elem2.odd[i] * elem1.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                size1 = size2;
                size2 = array_length(elem1.value,1);

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem1.value[i] > elem2.value[j] THEN
                            comp2 := comp2 + (elem1.odd[i] > elem2.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;
                return comp2 > comp1;

            -- Both numbers are expressed by trapezoids
            ELSE IF (elem1.type = False) and (elem2.type = False) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;

                FOR j IN 1..array_length(trapezoid_array_x_1,1) LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_2,1) LOOP
                        IF trapezoid_array_x_2[i] > trapezoid_array_x_1[j] THEN
                            comp1 := comp1 + (trapezoid_array_y_2[i] * trapezoid_array_y_1[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..array_length(trapezoid_array_x_2,1) LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_1,1) LOOP
                        IF trapezoid_array_x_1[i] > trapezoid_array_x_2[j] THEN
                            comp2 := comp2 + (trapezoid_array_y_1[i] * trapezoid_array_y_2[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp2 > comp1;
            END IF;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = False) and (elem2.type = True) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;

                FOR j IN 1..array_length(trapezoid_array_x_1,1) LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > trapezoid_array_x_1[j] THEN
                            comp1 := comp1 + (elem2.odd[i] * trapezoid_array_y_1[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..size2 LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_1,1) LOOP
                        IF trapezoid_array_x_1[i] > elem2.value[j] THEN
                            comp2 := comp2 + (trapezoid_array_y_1[i] * elem2.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp2 > comp1;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = True) and (elem2.type = False) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_2,1) LOOP
                        IF trapezoid_array_x_2[i] > elem1.value[j] THEN
                            comp1 := comp1 + (trapezoid_array_y_2[i] * elem1.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..array_length(trapezoid_array_x_2,1) LOOP
                    FOR i IN 1..size1 LOOP
                        IF elem1.value[i] > trapezoid_array_x_2[j] THEN
                            comp2 := comp2 + (elem1.odd[i] * trapezoid_array_y_2[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp2 > comp1;
            END IF;
        END IF;

        IF current_order = 2 THEN
            -- Both numbers are expressed by extension
            IF elem1.type and elem2.type THEN
                -- Traverse elem1 to calculate its mass moment and its mass
                FOR j IN 1..size1 LOOP
                    mass_moment_1   := mass_moment_1 + (elem1.value[j] * elem1.odd[j]);
                    mass_1          := mass_1 + elem1.odd[j];
                END LOOP;
                -- Traverse elem2 to calculate its mass moment and its mass
                FOR i IN 1..size2 LOOP
                    mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                    mass_2          := mass_2 + elem2.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_1 := mass_moment_1::float / mass_1;
                abscissa_2 := mass_moment_2::float / mass_2;
                RETURN abscissa_1 > abscissa_2;

            -- Both numbers are expressed by trapezoids
            ELSE IF (elem1.type = FALSE) AND (elem2.type = FALSE) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem1 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem1 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- Calculte both the abscissas
                abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
                abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
                abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

                abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
                abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
                abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_2);

                RETURN abscissa_1 > abscissa_2;
            END IF;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = FALSE) AND (elem2.type = TRUE) THEN
                -- elem1 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- Traverse elem2 to calculate its mass moment and its mass
                FOR i IN 1..size2 LOOP
                    mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                    mass_2          := mass_2 + elem2.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_2 := mass_moment_2::float / mass_2;

                abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
                abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
                abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

                RETURN abscissa_1 > abscissa_2;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = TRUE) AND (elem2.type = FALSE) THEN
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- Traverse elem1 to calculate its mass moment and its mass
                FOR i IN 1..size1 LOOP
                    mass_moment_1   := mass_moment_1 + (elem1.value[i] * elem1.odd[i]);
                    mass_1          := mass_1 + elem1.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_1 := mass_moment_1::float / mass_1;

                abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
                abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
                abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_1);

                RETURN abscissa_1 > abscissa_2;
            END IF;
        END IF;

        IF current_order = 3 THEN
            IF elem1.type and elem2.type THEN
                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > elem1.value[j] THEN
                            val := 0;

                            IF elem2.odd[i] > elem1.odd[j] THEN
                                val := elem1.odd[j];
                            ELSE
                                val := elem2.odd[i];
                            END IF;

                            IF val > comp1 THEN
                                comp1 := val;
                            END IF;

                            EXIT WHEN comp1 = 1;
                        END IF;
                    END LOOP;

                    EXIT WHEN comp1 = 1;
                END LOOP;

                size1 = size2;
                size2 = array_length(elem1.value,1);

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem1.value[i] > elem2.value[j] THEN
                            val := 0;

                            IF elem1.odd[i] > elem2.odd[j] THEN
                                val := elem2.odd[j];
                            ELSE
                                val := elem1.odd[i];
                            END IF;

                            IF val > comp2 THEN
                                comp2 := val;
                            END IF;

                            EXIT WHEN comp2 = 1;
                        END IF;
                    END LOOP;

                    EXIT WHEN comp2 = 1;
                END LOOP;

                return comp2 > comp1;

            ELSE IF (elem1.type = False) and (elem2.type = False) THEN
                IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
                    IF (elem1.value[2] is not Null) THEN
                        IF elem1.value[2] > elem2.value[3] THEN
                            return True;
                        END IF;

                        return False;			
                    END IF;
                    return False;
                END IF;

                IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
                    IF elem1.value[3] < elem2.value[2] THEN
                        return False;		
                    END IF;

                    IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
                        return False;
                    END IF;

                    IF elem1.value[2] > elem2.value[3]  THEN
                        return True;
                    END IF;

                    return False;
                END IF;	

                IF (elem1.value[3] is Null) THEN
                    IF (elem2.value[3] is null) THEN
                        return False;		
                    END IF;

                    IF elem1.value[2] > elem2.value[3] THEN
                        return True;
                    ELSE
                        return False;
                    END IF;	
                END IF;
            END IF;
            END IF;

            IF (elem1.type = False) and (elem2.type = True) THEN
                IF elem1.value[3] is Null THEN
                    return True;
                END IF;

                FOR i IN 1..size2 LOOP
                    IF elem2.odd[i] = 1 THEN
                        IF elem1.value[3] is not Null THEN
                            IF elem1.value[3] >= elem2.value[i] THEN
                                return True;
                            END IF;
                        END IF;
                    END IF;
                END LOOP;

                return False;
            END IF;

            IF (elem1.type = True) and (elem2.type = False) THEN
                IF elem2.value[2] is Null THEN
                    return True;
                END IF;

                FOR i IN 1..size1 LOOP
                    IF elem1.odd[i] = 1 THEN
                        IF elem2.value[2] is not Null THEN
                            IF elem2.value[2] <= elem1.value[i] THEN
                                return True;
                            END IF;
                        END IF;
                    END IF;
                END LOOP;

                return False;
            END IF;
        END IF;

    END;
$$ LANGUAGE plpgsql;

-- Definition of greater or equal element '>'
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_greater_eq(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        mass_moment_1   float := 0.0;
        mass_moment_2   float := 0.0;
        mass_1          float := 0.0;
        mass_2          float := 0.0;
        abscissa_1      float := 0.0;
        abscissa_2      float := 0.0;
        comp1           float := 0;
        comp2           float := 0;
        val             float := 0;
        size1           int := array_length(elem1.value,1);
        size2           int := array_length(elem2.value,1);
        current_order   int := 0;
        distance            int := 0;
        trapezoid_array_x_1 int[] := '{}';
        trapezoid_array_y_1 float[] := '{}';
        trapezoid_array_x_2 int[] := '{}';
        trapezoid_array_y_2 float[] := '{}';
        b                   float := 0;
        pendiente           float := 0;
        index               int := 1;
    BEGIN
        SELECT ordering INTO current_order FROM information_schema_fuzzy.current_orderings2;

        IF current_order = 1 THEN

            IF ((elem1.type = FALSE) AND (elem1.value[2] IS NOT NULL) AND (elem1.value[3] IS NOT NULL)) THEN
                distance := elem1.value[2] - elem1.value[1] -1;
                pendiente := 1::float / (elem1.value[2] - elem1.value[1]);
                b := -(pendiente*elem1.value[1]);

                for i in (elem1.value[1] + 1)..(elem1.value[1] + distance) loop
                    trapezoid_array_y_1[index] = (pendiente * i) + b;
                    trapezoid_array_x_1[index] = i;
                    index := index + 1;
                end loop;

                trapezoid_array_x_1[index] = elem1.value[2];
                trapezoid_array_y_1[index] = 1;
                index := index + 1;

                distance := elem1.value[3] - elem1.value[2] -1;
                for i in 1..distance loop
                    trapezoid_array_x_1[index] = elem1.value[2] + i;
                    trapezoid_array_y_1[index] = 1;
                    index := index + 1;
                end loop;

                trapezoid_array_x_1[index] = elem1.value[3];
                trapezoid_array_y_1[index] = 1;
                index := index + 1;

                distance := elem1.value[4] - elem1.value[3] -1;
                pendiente := (-1)::float / (elem1.value[4] - elem1.value[3]);
                b := -(pendiente*elem1.value[4]);
                for i in (elem1.value[3] + 1)..(elem1.value[3] + distance) loop
                    trapezoid_array_x_1[index] = i;
                    trapezoid_array_y_1[index] = (pendiente * i) + b;
                    index := index + 1;
                end loop;
            END IF;

            IF ((elem2.type = FALSE) AND (elem2.value[2] IS NOT NULL) AND (elem2.value[3] IS NOT NULL)) THEN
                index := 1;
                distance := elem2.value[2] - elem2.value[1] -1;
                pendiente := 1::float / (elem2.value[2] - elem2.value[1]);
                b := -(pendiente*elem2.value[1]);

                for i in (elem2.value[1] + 1)..(elem2.value[1] + distance) loop
                    trapezoid_array_y_2[index] = (pendiente * i) + b;
                    trapezoid_array_x_2[index] = i;
                    index := index + 1;
                end loop;

                trapezoid_array_x_2[index] = elem2.value[2];
                trapezoid_array_y_2[index] = 1;
                index := index + 1;

                distance := elem2.value[3] - elem2.value[2] -1;
                for i in 1..distance loop
                    trapezoid_array_x_2[index] = elem2.value[2] + i;
                    trapezoid_array_y_2[index] = 1;
                    index := index + 1;
                end loop;

                trapezoid_array_x_2[index] = elem2.value[3];
                trapezoid_array_y_2[index] = 1;
                index := index + 1;

                distance := elem2.value[4] - elem2.value[3] -1;
                pendiente := (-1)::float / (elem2.value[4] - elem2.value[3]);
                b := -(pendiente*elem2.value[4]);
                for i in (elem2.value[3] + 1)..(elem2.value[3] + distance) loop
                    trapezoid_array_x_2[index] = i;
                    trapezoid_array_y_2[index] = (pendiente * i) + b;
                    index := index + 1;
                end loop;
            END IF;

            -- Both numbers are expressed by extension
            IF elem1.type and elem2.type THEN
                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > elem1.value[j] THEN
                            comp1 := comp1 + (elem2.odd[i] * elem1.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                size1 = size2;
                size2 = array_length(elem1.value,1);

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem1.value[i] > elem2.value[j] THEN
                            comp2 := comp2 + (elem1.odd[i] * elem2.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;
                return comp2 >= comp1;

            -- Both numbers are expressed by trapezoids
            ELSE IF (elem1.type = False) and (elem2.type = False) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;

                FOR j IN 1..array_length(trapezoid_array_x_1,1) LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_2,1) LOOP
                        IF trapezoid_array_x_2[i] > trapezoid_array_x_1[j] THEN
                            comp1 := comp1 + (trapezoid_array_y_2[i] * trapezoid_array_y_1[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..array_length(trapezoid_array_x_2,1) LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_1,1) LOOP
                        IF trapezoid_array_x_1[i] > trapezoid_array_x_2[j] THEN
                            comp2 := comp2 + (trapezoid_array_y_1[i] * trapezoid_array_y_2[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp2 >= comp1;
            END IF;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = False) and (elem2.type = True) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;

                FOR j IN 1..array_length(trapezoid_array_x_1,1) LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > trapezoid_array_x_1[j] THEN
                            comp1 := comp1 + (elem2.odd[i] * trapezoid_array_y_1[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..size2 LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_1,1) LOOP
                        IF trapezoid_array_x_1[i] > elem2.value[j] THEN
                            comp2 := comp2 + (trapezoid_array_y_1[i] * elem2.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp2 >= comp1;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = True) and (elem2.type = False) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..array_length(trapezoid_array_x_2,1) LOOP
                        IF trapezoid_array_x_2[i] > elem1.value[j] THEN
                            comp1 := comp1 + (trapezoid_array_y_2[i] * elem1.odd[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                FOR j IN 1..array_length(trapezoid_array_x_2,1) LOOP
                    FOR i IN 1..size1 LOOP
                        IF elem1.value[i] > trapezoid_array_x_2[j] THEN
                            comp2 := comp2 + (elem1.odd[i] * trapezoid_array_y_2[j]);
                        END IF;
                    END LOOP;
                END LOOP;

                return comp2 >= comp1;
            END IF;
        END IF;

        IF current_order = 2 THEN
            -- Both numbers are expressed by extension
            IF elem1.type and elem2.type THEN
                -- Traverse elem1 to calculate its mass moment and its mass
                FOR j IN 1..size1 LOOP
                    mass_moment_1   := mass_moment_1 + (elem1.value[j] * elem1.odd[j]);
                    mass_1          := mass_1 + elem1.odd[j];
                END LOOP;
                -- Traverse elem2 to calculate its mass moment and its mass
                FOR i IN 1..size2 LOOP
                    mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                    mass_2          := mass_2 + elem2.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_1 := mass_moment_1::float / mass_1;
                abscissa_2 := mass_moment_2::float / mass_2;
                RETURN abscissa_1 >= abscissa_2;

            -- Both numbers are expressed by trapezoids
            ELSE IF (elem1.type = FALSE) AND (elem2.type = FALSE) THEN
                -- elem2 is a right shoulder trapezoid type
                IF (elem2.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem1 is a left shoulder trapezoid type
                IF (elem1.value[3] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- elem1 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- Calculte both the abscissas
                abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
                abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
                abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

                abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
                abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
                abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_2);

                RETURN abscissa_1 >= abscissa_2;
            END IF;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = FALSE) AND (elem2.type = TRUE) THEN
                -- elem1 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- Traverse elem2 to calculate its mass moment and its mass
                FOR i IN 1..size2 LOOP
                    mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                    mass_2          := mass_2 + elem2.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_2 := mass_moment_2::float / mass_2;

                abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
                abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
                abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

                RETURN abscissa_1 >= abscissa_2;
            END IF;

            -- One number is a trapezoid and the other one is by extension
            IF (elem1.type = TRUE) AND (elem2.type = FALSE) THEN
                -- elem2 is a left shoulder trapezoid type
                IF (elem2.value[3] IS NULL) THEN
                    RETURN FALSE;
                END IF;
                -- Traverse elem1 to calculate its mass moment and its mass
                FOR i IN 1..size1 LOOP
                    mass_moment_1   := mass_moment_1 + (elem1.value[i] * elem1.odd[i]);
                    mass_1          := mass_1 + elem1.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_1 := mass_moment_1::float / mass_1;

                abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
                abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
                abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_1);

                RETURN abscissa_1 >= abscissa_2;
            END IF;
        END IF;

        IF current_order = 3 THEN
            IF elem1.type and elem2.type THEN
                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem2.value[i] > elem1.value[j] THEN
                            val := 0;

                            IF elem2.odd[i] > elem1.odd[j] THEN
                                val := elem1.odd[j];
                            ELSE 
                                val := elem2.odd[i];
                            END IF;

                            IF val > comp1 THEN
                                comp1 := val;
                            END IF;

                            EXIT WHEN comp1 = 1;
                        END IF;
                    END LOOP;

                    EXIT WHEN comp1 = 1;
                END LOOP;

                size1 = size2;
                size2 = array_length(elem1.value,1);

                FOR j IN 1..size1 LOOP
                    FOR i IN 1..size2 LOOP
                        IF elem1.value[i] > elem2.value[j] THEN
                            val := 0;

                            IF elem1.odd[i] > elem2.odd[j] THEN
                                val := elem2.odd[j];
                            ELSE 
                                val := elem1.odd[i];
                            END IF;

                            IF val > comp2 THEN
                                comp2 := val;
                            END IF;

                            EXIT WHEN comp2 = 1;
                        END IF;
                    END LOOP;

                    EXIT WHEN comp2 = 1;
                END LOOP;

                return comp2 >= comp1;

            ELSE IF (elem1.type = False) and (elem2.type = False) THEN
                IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
                    IF (elem1.value[2] is not Null) THEN
                        IF elem1.value[2] > elem2.value[3] THEN
                            return True;
                        END IF;
                        return True;			
                    END IF;
                return True;
                END IF;

                IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
                    IF elem1.value[3] < elem2.value[2] THEN
                        return False;		
                    END IF;

                    IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
                        return True;
                    END IF;

                    IF elem1.value[2] > elem2.value[3]  THEN
                        return True;
                    END IF;

                    return True;
                END IF;	

                IF (elem1.value[3] is Null) THEN
                    IF (elem2.value[3] is null) THEN
                        return True;		
                    END IF;

                    IF elem1.value[2] > elem2.value[3] THEN
                        return True;
                    ELSE
                        return True;
                    END IF;	
                END IF;
            END IF;
            END IF;

            IF (elem1.type = False) and (elem2.type = True) THEN
                IF elem1.value[3] is Null THEN
                    return True;
                END IF;

                FOR i IN 1..size2 LOOP
                    IF elem2.odd[i] = 1 THEN
                        IF elem1.value[3] is not Null THEN
                            IF elem1.value[3] >= elem2.value[i] THEN
                                return True;
                            END IF;
                        END IF;
                    END IF;
                END LOOP;

                return False;
            END IF;

            IF (elem1.type = True) and (elem2.type = False) THEN
                IF elem2.value[2] is Null THEN
                    return True;
                END IF;

                FOR i IN 1..size1 LOOP
                    IF elem1.odd[i] = 1 THEN
                        IF elem2.value[2] is not Null THEN
                            IF elem2.value[2] <= elem1.value[i] THEN
                                return True;
                            END IF;
                        END IF;
                    END IF;
                END LOOP;

                return False;
            END IF;
        END IF;
    END;
$$ LANGUAGE plpgsql;