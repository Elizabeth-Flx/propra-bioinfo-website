# Jmol Reference
Jmol is a Java applet that can be used to visualize proteins in 3D space.

## Loading Proteins
You can open a protein under “file > open/open url/get pdb/get mol” from the applet. You have options to either load a protein from an online database using a PDB ID.
Alternatively you can load files using the script
```
load <filepath>
```
or 
```
load <weblink>
```

## Background
You can set the background color using
```
background <color>
```
## Views
You can set the protein to spin using
```
spin <on/off>
```
You can enable/disable wireframe (connections between proteins) using
```
wireframe <on/off/only>
```
You can enable/disable protein backbone using
```
backbone <on/off/only>
```
You can enable/disable secondary structures using
```
cartoon <on/off/only>
```

## Coloring
Using select you can select certain parts (such as helix, sheet, etc…) of the protein to interact with later
```
select <element>
```
You can then set their color using
```
color <color>
```

## Export
You can export the protein in many different file formats (you can find all of them on their website), a few common ones are png, jpeg, mol, etc…
```
write <filetype> <path>
```


