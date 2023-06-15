
package br.edu.ifsul.cc.lpoo.om;

import br.edu.ifsul.cc.lpoo.om.gui.JFramePrincipal;
import br.edu.ifsul.cc.lpoo.om.gui.JMenuBarHome;
import br.edu.ifsul.cc.lpoo.om.gui.JPanelHome;
import br.edu.ifsul.cc.lpoo.om.gui.autenticacao.JPanelAutenticacao;
import br.edu.ifsul.cc.lpoo.om.gui.funcionario.JPanelFuncionario;
import br.edu.ifsul.cc.lpoo.om.model.Funcionario;
import br.edu.ifsul.cc.lpoo.om.model.dao.PersistenciaJDBC;
import javax.swing.JOptionPane;

/**
 *
 * @author telmo
 */
public class Controle {
    
    private PersistenciaJDBC conexaoJDBC;
    
    private JFramePrincipal frame;
    
    private JPanelAutenticacao telaAutenticacao;
    
    private JMenuBarHome menuBar;
    
    private JPanelFuncionario telaFuncionario;
    
    private JPanelHome telaHome;
    
    
    protected Controle(){
        
    }
    
    public void autenticar(String cpf, String senha){
        
        try{
            Funcionario f =  getConexaoJDBC().doLogin(cpf, senha);
            
            if(f != null){

                JOptionPane.showMessageDialog(telaAutenticacao, "Funcionário "+f.getNome()+" autenticado com sucesso!", "Autenticação", JOptionPane.INFORMATION_MESSAGE);

                frame.setJMenuBar(menuBar);//adiciona o menu de barra no frame
                frame.showTela("tela_home");//muda a tela para o painel de boas vindas (home)

            }else{

                JOptionPane.showMessageDialog(telaAutenticacao, "Dados inválidos!", "Autenticação", JOptionPane.INFORMATION_MESSAGE);
            }

        }catch(Exception e){

            JOptionPane.showMessageDialog(telaAutenticacao, "Erro ao executar a autenticação no Banco de Dados!", "Autenticação", JOptionPane.ERROR_MESSAGE);
        }        
        
    }
    
    public void initComponents(){
        
        //inicialização do atributo da instância -> frame
        frame = new JFramePrincipal(this);
        
        telaAutenticacao = new JPanelAutenticacao(this);//incializando o atributo da instância.
        
        frame.addTela(telaAutenticacao, "tela_autenticacao");//adicionando uma carta (painel) no baralho (jframe)
        frame.showTela("tela_autenticacao");//coloca em primeiro plano a carta.
        
        menuBar = new JMenuBarHome(this);
        
        telaHome = new JPanelHome(this);
        frame.addTela(telaHome, "tela_home");
        
        telaFuncionario = new JPanelFuncionario(this);
        frame.addTela(telaFuncionario, "tela_funcionario");
        
        
        frame.setVisible(true);
        
    }
    
    public boolean conectarBD() throws Exception {

            conexaoJDBC = new PersistenciaJDBC();

            if(getConexaoJDBC()!= null){

                        return getConexaoJDBC().conexaoAberta();
            }

            return false;
    }
    
    public void fecharBD(){

        System.out.println("Fechando conexao com o Banco de Dados");
        getConexaoJDBC().fecharConexao();

    }

    /**
     * @return the conexaoJDBC
     */
    public PersistenciaJDBC getConexaoJDBC() {
        return conexaoJDBC;
    }
        
    
    public void showTela(String nomeTela){
         
        //para cada nova tela de CRUD adicionar um elseif
        
         if(nomeTela.equals("tela_funcionario")){
            telaFuncionario.showTela("tela_funcionario_listagem");
            frame.showTela(nomeTela);
            
         }
         
    }

    
}
