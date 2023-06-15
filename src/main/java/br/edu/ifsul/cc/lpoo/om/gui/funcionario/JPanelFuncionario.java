
package br.edu.ifsul.cc.lpoo.om.gui.funcionario;

import br.edu.ifsul.cc.lpoo.om.Controle;
import java.awt.CardLayout;
import javax.swing.JPanel;

/**
 *
 * @author telmo
 */
public class JPanelFuncionario  extends JPanel {
    
    private CardLayout cardLayout;
    private Controle controle;
    
    private JPanelFuncionarioFormulario formulario;
    private JPanelFuncionarioListagem listagem;
    
    public JPanelFuncionario(Controle controle){
        
        this.controle = controle;
        initComponents();
    }
    
     private void initComponents(){
        
        cardLayout = new CardLayout();
        this.setLayout(cardLayout);
        
        formulario = new JPanelFuncionarioFormulario(this, controle);
        listagem = new JPanelFuncionarioListagem(this, controle);
        
        this.add(getFormulario(), "tela_funcionario_formulario");
        this.add(listagem, "tela_funcionario_listagem");
                
    }   
     
    public void showTela(String nomeTela){
        
        if(nomeTela.equals("tela_funcionario_listagem")){
            
            listagem.populaTable();
            
        }else if(nomeTela.equals("tela_funcionario_formulario")){
            
            getFormulario().populaComboCargo();
            
        }
        
        cardLayout.show(this, nomeTela);
        
    }
    
     /**
     * @return the controle
     */
    public Controle getControle() {
        return controle;
    }

    /**
     * @return the formulario
     */
    public JPanelFuncionarioFormulario getFormulario() {
        return formulario;
    }
        
}
