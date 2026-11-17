# Aufgabe 11: Inzidenzen: Modellierung und Suche von Teilsequenzen

Das Verhältnis (fc) zwischen den tatsächlichen und erwarteten Inzidenzen gibt an, wie stark die Inzidenzen von den erwarteten Werten abweichen. 

fc = 1 : tatsächlichen und erwarteten Inzidenzen gleich sind
fc > 1 : tatsächlichen Inzidenzen höher als erwartet 
fc < 1 : tatsächlichen Inzidenzen niedriger als erwartet

Man kann dieses Verhältnis nutzen um möglicher funktionale Bedeutungen der Seauenzen zu finden. Zum Beispiel könnten überdurchschnittliche Inzidenzen darauf hinweisen, dass diese Patterns eine Rolle bei der Genexpression oder RNA-Replikation hinweisen, wären unterduchschnittliche Inzidenzen auf auf eine weniger wichtige Rolle hinweisen können.


## Bsp.:

Zum Beispiel bei unsere Aufgabe haben wir gesehen das die Teilsequenz "AACCCTGTC" nicht sonderlich oft bei des Genom von "Mycobacterium Tuberculosis" vorkommt (fc=0.4),aber dafür die Seuqenz im Menschlichen Genom besonders oft vorkommt (fc=9.3). Vielleicht könnte man diese Teilsequenz nutzen um zwischen das Genom vom Menschen und Tuberkulose zu unterscheiden,
dies könnte vielleicht bei der Medikamentenentwicklung relevant sein. 

Man könnte auch die Teilsequenz "CG" nutzen um die Genome zu unterscheiden, da dieser in Tuberkulose überrepräsentiert ist (fc=2.1) und im Menschen unterrepräsentiert ist (fc=0.2).



Man kann auch den unterschiedlichen GC Content zwischen den Genomen nutzten um die zu unterscheiden, da Tuberkulose ein GC Content von 65.61% hat und der Mensch ein CG Content von 40.16%.