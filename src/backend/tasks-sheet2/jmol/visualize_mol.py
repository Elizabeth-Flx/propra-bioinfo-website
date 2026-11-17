#!/usr/bin/env python3
import os
import argparse

def run_jmol_script(pbd_id, output, html, colourized):

    jmol_path = r".\files\Jmol.jar"
    jmol_script_path = r".\files\jmol_script.txt"

    if html:
        write_jmol_script_to_html(pbd_id, output, colourized)
    else:
        write_jmol_script_to_text(pbd_id, output, colourized)
        os.system("%s -n -s %s" % (jmol_path, jmol_script_path))



def write_jmol_script_to_text(pbd_id, output_path, colourized):
    fmol_script_file = open("./files/jmol_script.txt", 'w')
    
    script = "load https://files.rcsb.org/view/%s.pdb; " % (pbd_id)
    script += "cartoon only; "
    
    if colourized:
        script += "select helix; color orange; select sheet; color cyan; "
        
    script += "write PNG %s" % (output_path + "molecule.png")

    fmol_script_file.write(script)
    fmol_script_file.close()

def write_jmol_script_to_html(pbd_id, output_path, colourized):
    template_html_file = open("./files/template.html", 'r')
    fmol_html_file = open("%s" % (output_path + "molecule.html"), 'w')

    html = template_html_file.read().replace(":::PLACEHOLDER:::", pbd_id)
    if colourized:
        html = html.replace(":::COLOR:::","select helix; color orange; select sheet; color cyan;")
    else:
        html = html.replace(":::COLOR:::","")

    fmol_html_file.write(html)
    fmol_html_file.close()
    template_html_file.close()


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Visualize PDB files in Jmol')

    parser.add_argument('--id', type=str, required=True, help='PDB ID')
    parser.add_argument('--output', type=str, default='./', help='Output file path')
    parser.add_argument('--html', action='store_true', help='Output HTML embedding code')
    parser.add_argument('--colourized', action='store_true', help='Colorize secondary structures')

    args = vars(parser.parse_args())

    run_jmol_script(args['id'], args['output'], args['html'], args['colourized'])
    