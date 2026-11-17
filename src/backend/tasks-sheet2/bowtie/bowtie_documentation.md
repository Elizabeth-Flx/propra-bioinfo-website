### Bowtie-Task Dokumentation
## Allgemeines zu Bowtie
- Bowtie ist ein schneller, memory-effizienter Short-Read-Aligner
- besonders gut zum Alignen vieler kurzer Sequenzen geeignet auf ein gro
- Indext das Genom mit Burrows-Wheeler ( String Compression, String mit sich wiederholenden Stellen gut geeignet )
- kann als Output das SAM Format ausgeben, das in anderen Programmen weiter verwendet werden kann ( z.B Samtools)
- kein Report von Gapped Alignments ( mit Lücken )
## Parameter von Bowtie
# -a, -k, -m, -M, --best, --strata: Auswahl welche Alignments im Report erscheinen sollen
-a: "Alle Alignments sollen im Report erscheinen"
-k: "Hinter k kann ein Integer angegeben werden, der die Anzahl der Alignments die im Report erscheinen sollen angibt" (-k 1 ist default wenn keines der obigen Parameter verwendet wurde)
-a --best: " wie in -a aber mit garantierter best-to-worst order
-a --best --strata: "nur die alignments die im best alignment stratum enthalten sind (die wenigstens mismatches)
-m (in Kombination mit k): "Wenn garantiert werden soll das angeziegt alignments unique sind"
# -v und -n Alignment Mode
-v: in v mode dürfen Alignments nicht mehr als v mismatches haben (v: Wert zwischen 0-3)
-n: nicht mehr als n mismatches in den ersten l Basen, phred quality der mismatches darf e nicht übersteigen (default 40)
# Bowtie Command um Crispr-Cas Sequezen zu alignen
./bowtie --all -v 3 indexes/hg19/bt -f /mnt/biocluster/praktikum/bioprakt/progprakt-13/_repository/src/backend/tasks/crispr_cas/output_files/output_crispr_cas.fasta -S /mnt/biocluster/praktikum/bioprakt/progprakt-13/bowtie/output.sam 
- v 3 gibt an, dass maximal 3 Mismatches erlaubt sind
indexes/hg19/bt ist der Pfad zu den Index-Files des Genms
-f Input ist eine Fasta-File
-S Output im SAM-Format zu dem gegebenen Pfad
output.sam ist in progprakt-13/bowtie/output.sam
