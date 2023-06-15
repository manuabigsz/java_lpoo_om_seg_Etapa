
package br.edu.ifsul.cc.lpoo.om.model.dao;

import br.edu.ifsul.cc.lpoo.om.model.Cargo;
import br.edu.ifsul.cc.lpoo.om.model.Cliente;
import br.edu.ifsul.cc.lpoo.om.model.Curso;
import br.edu.ifsul.cc.lpoo.om.model.Equipe;
import br.edu.ifsul.cc.lpoo.om.model.Funcionario;
import br.edu.ifsul.cc.lpoo.om.model.Peca;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author telmo junior
 * @data 30/03/2023
 * 
 * Classe que implementará InterfacePersistencia e utilização
 * de JDBC para a persistência dos dados.
 */
public class PersistenciaJDBC implements InterfacePersistencia {
    
    private final String DRIVER = "org.postgresql.Driver";
    private final String USER = "postgres";
    private final String SENHA = "postgres";
    public static final String URL = "jdbc:postgresql://localhost:5432/db_om_2023_1";
    private Connection con = null;

    
    public PersistenciaJDBC() throws Exception {
        
        Class.forName(DRIVER); //carregamento do driver postgresql em tempo de execução
        System.out.println("Tentando estabelecer conexao JDBC com : "+URL+" ...");
            
        this.con =  DriverManager.getConnection(URL, USER, SENHA);         
    }

    @Override
    public Boolean conexaoAberta() {
        
        try {
            if(con != null)
                return !con.isClosed();//verifica se a conexao está aberta
        } catch (SQLException ex) {
           ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void fecharConexao() {
        
        try{                               
            this.con.close();//fecha a conexao.
            System.out.println("Fechou conexao JDBC");
        }catch(SQLException e){            
            e.printStackTrace();//gera uma pilha de erro na saida.
        } 
    }

    @Override
    public Object find(Class c, Object id) throws Exception {
        
            if(c == Peca.class){
                
                //select na tb_peca
                PreparedStatement ps = 
                        this.con.prepareStatement("select "
                                                        + "id, "
                                                        + "fornecedor, "
                                                        + "nome, "
                                                        + "valor "
                                                        + "from tb_peca "
                                                        + "where id = ? ");
                        //o 1 representa o primeiro parâmetro (?)                                
                        ps.setInt(1, Integer.valueOf(id.toString()));
                        
                //executa o comando SQL (select)
                ResultSet rs = ps.executeQuery();
                //o método next recupera a proxima linha do resultado,
                //se exitir uma linha retorna verdadeiro
                //se nao existir uma linha retorna false.
                if(rs.next()){
                    
                    Peca p = new Peca();
                        p.setId(rs.getInt("id"));//recupera pelo nome da coluna
                        p.setNome(rs.getString("nome"));
                        p.setFornecedor(rs.getString("fornecedor"));
                        p.setValor(rs.getFloat("valor"));
                        
                    rs.close();//fecha o cursor do BD para essa consulta
                    return p;//retorna o objeto do tipo Peca.
                }
                        
                
            }else if (c == Funcionario.class){
                
                //select na tb_pessoa e tb_funcionario
                PreparedStatement ps = 
                        this.con.prepareStatement("select c.descricao, p.data_nascimento, p.numero, f.data_admissao, f.cpf, p.cep, f.numero_ctps, p.nome, p.complemento, p.senha, f.cargo "
                                                + "from tb_pessoa p, tb_funcionario f left join tb_cargo c on (f.cargo = c.id)"
                                                + "where f.cpf=p.cpf and f.cpf = ? ");
                
                ps.setString(1, id.toString());
                 
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    
                    Funcionario f = new Funcionario();
                    f.setCpf(rs.getString("cpf"));
                    f.setNumero_ctps(rs.getString("numero_ctps"));
                    f.setNome(rs.getString("nome"));
                    f.setCep(rs.getString("cep"));
                    f.setNumero(rs.getString("numero"));
                    f.setComplemento(rs.getString("complemento"));
                    f.setSenha(rs.getString("senha"));                   
                        Calendar cld = Calendar.getInstance();
                        cld.setTimeInMillis(rs.getDate("data_admissao").getTime());
                    f.setData_admissao(cld);
                    if(rs.getDate("data_nascimento") != null){
                        cld = Calendar.getInstance();
                        cld.setTimeInMillis(rs.getDate("data_nascimento").getTime());
                    }
                    f.setData_nascimento(cld);
                        Cargo cg = new Cargo();
                        cg.setId(rs.getInt("cargo"));
                        cg.setDescricao(rs.getString("descricao"));
                    f.setCargo(cg);
                
                
                    
                    PreparedStatement psCursos = 
                        this.con.prepareStatement("select c.descricao, c.id "
                                                + "from tb_funcionario f, tb_curso c, tb_funcionario_curso fc "
                                                + "where f.cpf=fc.pessoa_cpf and c.id=fc.curso_id and f.cpf = ? ");
                    
                    psCursos.setString(1, id.toString());
                    
                    ResultSet rsCursos = psCursos.executeQuery();
                    List<Curso> listCursos = new ArrayList();
                    while(rsCursos.next()){
                        
                        //criar um objeto do tipo curso
                        //setar a descricao e o id do curso
                        //adicionar o objeto curso na lista
                        
                        Curso cs = new Curso();
                        cs.setId(rsCursos.getInt("id"));
                        cs.setDescricao("descricao");
                        
                        listCursos.add(cs);
                    }
                    
                    f.setCursos(listCursos);
                    
                    rsCursos.close();
                    return f;
                }
                
                rs.close();
                
                
            }else if (c == Curso.class){
                
                 Curso crs = new Curso();
                 
                 crs.getData_conclusao();
                 
                 ResultSet rs = null;
                 
                 
                 //criacao de uma variável auxiliar do tipo java.util.Calendar
                 Calendar cl = Calendar.getInstance();
                 
                 //recuperando um long do java.sql.Date
                 //setando o long no Calendar.
                 cl.setTimeInMillis(rs.getDate("data_conclusao").getTime());
                 
                 //setando o Calendar no Curso.
                 crs.setData_conclusao(cl);
                 
                 
                
                //select na tb_curso
                
            }else if (c == Cargo.class){
                
                Cargo cg = null;
                
                PreparedStatement ps = 
                        this.con.prepareStatement("select "
                                                        + "id, "
                                                        + "descricao "
                                                        + "from tb_cargo "
                                                        + "where id = ? ");
                        //o 1 representa o primeiro parâmetro (?)                                
                        ps.setInt(1, Integer.valueOf(id.toString()));
                        
               ResultSet rs = ps.executeQuery();
               if(rs.next()){
                   cg = new Cargo();
                   cg.setId(rs.getInt("id"));
                   cg.setDescricao(rs.getString("descricao"));
               }
               
               return cg;
                
            }else if (c == Cliente.class){
                
                
            }//...
    
            return null;
    }

    @Override
    public void persist(Object o) throws Exception {        
        if(o instanceof Peca){
            //conversao (casting "modela") de Object para Peca.
            Peca p = (Peca) o;            
            //testa para descobrir se existe informação na chave primaria
            if(p.getId() == null){
                //insert into tb_peca...  
                PreparedStatement ps = this.con.prepareStatement("insert into tb_peca "
                                                                        + "(id, "
                                                                        + "fornecedor, "
                                                                        + "nome, "
                                                                        + "valor) "
                                                                        + "values ("
                        + "nextval('seq_peca_id'), "
                        + "?, "
                        + "?, "
                        + "?);");
                        
                        ps.setString(1, p.getFornecedor());
                        ps.setString(2, p.getNome());
                        ps.setFloat(3, p.getValor());
                //executa o insert.        
                ps.execute();
                
                //fecha o cursor
                ps.close();
                
            }else{                
                //update tb_peca set ...
                 PreparedStatement ps = this.con.prepareStatement("update tb_peca set "
                                                                        + "fornecedor = ?, "
                                                                        + "nome = ?, "
                                                                        + "valor = ? "
                                                                        + "where "
                                                                        + "id = ? ");
                        
                        ps.setString(1, p.getFornecedor());
                        ps.setString(2, p.getNome());                                                
                        ps.setFloat(3, p.getValor());
                        ps.setInt(4, p.getId());
                        
                        
                //executa o insert.        
                ps.execute();
                
                //fecha o cursor
                ps.close();
            }            
        }else if(o instanceof Cargo ){            
            Cargo  c = (Cargo) o;
            if(c.getId() == null){
                
                PreparedStatement ps = this.con.prepareStatement("insert into tb_cargo (id, descricao) "
                                                                             + "values "
                                                                             + "(nextval('sequence_cargo_id', "
                                                                             + "?); ");
                
                ps.setString(1, c.getDescricao());
                   
            }else{
                
            }            
        }else if( o instanceof Funcionario){
            
            Funcionario f = (Funcionario) o;
            //utilizando a data_admissao, pois será gerada pelo próprio BD.
            //nesse caso, quando não houver data_admissao realizará insert.
            if(f.getData_admissao() == null){
                
                PreparedStatement ps = this.con.prepareStatement("insert into tb_pessoa (cpf, nome, numero, senha, complemento, tipo, cep, data_nascimento) values "
                                                                 + "("
                                                                 + "?, "
                                                                 + "?, "
                                                                 + "?, "
                                                                 + "?, "
                                                                 + "?, "
                                                                 + "?, "
                                                                 + "?, "
                                                                 + "? "
                                                             
                                                                 + ")");
              
                ps.setString(1, f.getCpf());
                ps.setString(2, f.getNome());
                ps.setString(3, f.getNumero());
                ps.setString(4, f.getSenha());
                ps.setString(5, f.getComplemento());
                ps.setString(6, "F");
                ps.setString(7, f.getCep());
                if(f.getData_nascimento() != null)
                    ps.setDate(8, new java.sql.Date(f.getData_nascimento().getTimeInMillis()));
                else
                    ps.setDate(8, null);
                ps.execute();
                ps.close();
                
                ps = this.con.prepareStatement("insert into tb_funcionario (data_admissao, numero_ctps, cpf, cargo) values "
                                                                 + "("
                                                                 + "now(), "
                                                                 + "?, "
                                                                 + "?, "
                                                                 + "? "                                                              
                                                                 + ")");
                ps.setString(1, f.getNumero_ctps());
                ps.setString(2, f.getCpf());
                ps.setInt(3, f.getCargo().getId());
                ps.execute();
                
                ps.close();
                
                if(f.getCursos() != null && !f.getCursos().isEmpty()){
                    
                    for(Curso curso : f.getCursos()){
                        
                        PreparedStatement ps2 = this.con.prepareStatement("insert into tb_funcionario_curso (pessoa_cpf, curso_id) values "
                                                                 + "("
                                                                 + "?, "
                                                                 + "? "                                                                                                                               
                                                                 + ")");
                        ps2.setString(1, f.getCpf());
                        ps2.setInt(2, curso.getId());
                        
                        ps2.execute();
                        
                        ps2.close();
                        
                    }
                    
                  
                }
                
            }else{
                
                PreparedStatement ps = this.con.prepareStatement("update tb_pessoa set nome = ?, numero = ?, senha = ?, complemento = ?, cep = ?, data_nascimento = ? where cpf = ? ");
                
                ps.setString(1, f.getNome());
                ps.setString(2, f.getNumero());
                ps.setString(3, f.getSenha());
                ps.setString(4, f.getComplemento());               
                ps.setString(5, f.getCep());
                if(f.getData_nascimento() != null){
                    ps.setDate(6, new java.sql.Date(f.getData_nascimento().getTimeInMillis()));
                }else{
                    ps.setDate(6, null);
                }
               
                ps.setString(7, f.getCpf());               
                ps.execute();
                ps.close();
                
                ps = this.con.prepareStatement("update tb_funcionario set data_admissao = ?, numero_ctps = ?, cargo = ? where cpf = ? ");
                if(f.getData_admissao() != null){
                    ps.setDate(1, new java.sql.Date(f.getData_admissao().getTimeInMillis()));
                }else{
                    ps.setString(1, null);
                }
                
                ps.setString(2, f.getNumero_ctps());            
                ps.setInt(3, f.getCargo().getId());
                ps.setString(4, f.getCpf());        
                ps.execute();
                
                ps.close();
                
                ps = this.con.prepareStatement("delete from tb_funcionario_curso where pessoa_cpf = ?");
                ps.setString(1, f.getCpf()); 
                ps.execute();
                
                ps.close();
                
                if(f.getCursos() != null && !f.getCursos().isEmpty()){
                    
                    for(Curso curso : f.getCursos()){
                        
                        PreparedStatement ps2 = this.con.prepareStatement("insert into tb_funcionario_curso (pessoa_cpf, curso_id) values "
                                                                 + "("
                                                                 + "?, "
                                                                 + "? "                                                                                                                               
                                                                 + ")");
                        ps2.setString(1, f.getCpf());
                        ps2.setInt(2, curso.getId());
                        
                        ps2.execute();
                        
                        ps2.close();
                        
                    }
                    
                  
                }
                
                
            }            
        }else if( o instanceof Cliente){
            
            Cliente c = (Cliente) o;
            if(c.getObservacoes() == null){
                
            }else{
                
            }
            
        }else if (o instanceof Equipe){
            Equipe e = (Equipe) o;
            
            if(e.getId() == null){
                
                    PreparedStatement ps = this.con.prepareStatement("insert into tb_equipe (id, nome, especialidades) "
                                                                             + "values "
                                                                             + "(nextval('seq_equipe_id'), "
                                                                             + "?, ?) returning id; ");                                   
                  
                    ps.setString(1, e.getNome());
                    ps.setString(2, e.getEspecialidades());
                    
                    ResultSet rs = ps.executeQuery();
                    
                    if(rs.next()){
                        
                        e.setId(rs.getInt("id"));
                        
                        if(e.getFuncionarios() != null && !e.getFuncionarios().isEmpty()){

                            for(Funcionario f : e.getFuncionarios()){

                                PreparedStatement ps2 = this.con.prepareStatement("insert into tb_equipe_funcionario (equipe_id, funcionario_cpf) "
                                                                                 + "values "
                                                                                 + "(?, "
                                                                                 + "?); ");                                   
                                ps2.setInt(1, e.getId());
                                ps2.setString(2, f.getCpf());

                                ps2.execute();

                                ps2.close();
                            }
                        }
                                 
                    }
                    
                    
           
                
            }else{
                
                
            }
            
        }
        
    }

    @Override
    public void remover(Object o) throws Exception {
        
        if(o instanceof Peca){
            Peca p = (Peca) o;
            
            PreparedStatement ps = this.con.prepareStatement("delete "
                                                            + "from "
                                                            + "tb_peca "
                                                            + "where "
                                                            + "id = ?");
            
                ps.setInt(1, p.getId());
            //executa o delete
            ps.execute();
            
            //fecha o cursor.
            ps.close();
                
        }else if(o instanceof Cargo){
            Cargo c = (Cargo) o;
            
        }else if(o instanceof Funcionario){
            
            Funcionario f = (Funcionario) o;
            
            
            PreparedStatement ps = this.con.prepareStatement("delete "
                                                            + "from "
                                                            + "tb_funcionario_curso "
                                                            + "where "
                                                            + "pessoa_cpf = ?");
            
            ps.setString(1, f.getCpf());
            //executa o delete
            ps.execute();
            
            //fecha o cursor.
            ps.close();
            
            
            ps = this.con.prepareStatement("delete "
                                                            + "from "
                                                            + "tb_funcionario "
                                                            + "where "
                                                            + "cpf = ?");
            
            ps.setString(1, f.getCpf());
            //executa o delete
            ps.execute();
            
            //fecha o cursor.
            ps.close();
            
            ps = this.con.prepareStatement("delete "
                                                            + "from "
                                                            + "tb_pessoa "
                                                            + "where "
                                                            + "cpf = ?");
            
            ps.setString(1, f.getCpf());
            //executa o delete
            ps.execute();
            
            //fecha o cursor.
            ps.close();
            
            
        }else if(o instanceof Equipe){
            
            Equipe e = (Equipe) o;
            PreparedStatement ps = this.con.prepareStatement("delete from tb_equipe_funcionario where equipe_id = ?; ");
          
            ps.setInt(1, e.getId());
            
            ps.execute();
            
            ps.close();
            
            ps = this.con.prepareStatement("delete from tb_equipe where id = ?; ");
          
            ps.setInt(1, e.getId());
            
            ps.execute();
            
            ps.close();
            
        }
        
    }

    @Override
    public Funcionario doLogin(String cpf, String senha) throws Exception {
       
        
        Funcionario func = null;
        
        PreparedStatement ps = 
            this.con.prepareStatement("select p.nome, f.numero_ctps from tb_pessoa p, tb_funcionario f where p.cpf=f.cpf and p.cpf = ? and p.senha = ? and p.tipo = 'F';");
                        
            ps.setString(1, cpf);
            ps.setString(2, senha);
            
            ResultSet rs = ps.executeQuery();//o ponteiro do REsultSet inicialmente está na linha -1
            
            if(rs.next()){//se a matriz (ResultSet) tem uma linha

                func = new Funcionario();
                func.setNome(rs.getString("nome"));                
            }
        
            ps.close();
            return func; 
        
    }

    @Override
    public List<Peca> listPecas() throws Exception {
        
        List<Peca> listagemRetorno;
        
        //select na tb_peca
        PreparedStatement ps = 
                this.con.prepareStatement("select "
                                                + "id, "
                                                + "fornecedor, "
                                                + "nome, "
                                                + "valor "
                                                + "from tb_peca "
                                                + "order by id asc ");
               
        //executa o comando SQL (select)
        ResultSet rs = ps.executeQuery();
        
        listagemRetorno = new ArrayList();
        
        //o método next recupera a proxima linha do resultado,
        //se exitir uma linha retorna verdadeiro
        //se nao existir uma linha retorna false.
        while(rs.next()){

            Peca p = new Peca();
                p.setId(rs.getInt("id"));//recupera pelo nome da coluna
                p.setNome(rs.getString("nome"));
                p.setFornecedor(rs.getString("fornecedor"));
                p.setValor(rs.getFloat("valor"));

            listagemRetorno.add(p);     
            
        }
        
        rs.close();//fecha o cursor do BD para essa consulta
        
        return listagemRetorno;//retorna a lista.
    }

    @Override
    public List<Funcionario> listFuncionario() throws Exception {
        
        
        List<Funcionario> listagemRetorno;
        
        //select na tb_equipe
        //to_char(p.data_nascimento, 'dd/MM/yyyy') as 
        //to_char(f.data_admissao, 'dd/MM/yyyy') as 
        PreparedStatement ps = 
                this.con.prepareStatement("select p.cpf, p.cep, p.complemento, p.data_nascimento, p.nome, p.numero, f.numero_ctps, f.data_admissao, f.cargo from tb_pessoa p, tb_funcionario f where p.cpf=f.cpf order by f.data_admissao asc;");
        
           //executa o comando SQL (select)
        ResultSet rs = ps.executeQuery();
        
        listagemRetorno = new ArrayList();
        
        while(rs.next()){
            
            Funcionario f = new Funcionario();
                f.setCpf(rs.getString("cpf"));
                f.setCep(rs.getString("cep"));
                f.setComplemento(rs.getString("complemento"));
                if(rs.getDate("data_nascimento") != null){
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(rs.getDate("data_nascimento").getTime());
                    f.setData_nascimento(c);
                }
               
                f.setNome(rs.getString("nome"));
                f.setNumero(rs.getString("numero"));
                f.setNumero_ctps(rs.getString("numero_ctps"));
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(rs.getDate("data_admissao").getTime());
                f.setData_admissao(c);
                    Cargo cg = new Cargo();
                    cg.setId(rs.getInt("cargo"));
                f.setCargo(cg);
            
                listagemRetorno.add(f);
        }
        rs.close();
        
        return listagemRetorno;
    }

    @Override
    public List<Curso> listCurso() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Cargo> listCargo() throws Exception {
       
        List<Cargo> listagemRetorno;
        
        //select na tb_equipe
        //to_char(p.data_nascimento, 'dd/MM/yyyy') as 
        //to_char(f.data_admissao, 'dd/MM/yyyy') as 
        PreparedStatement ps = 
                this.con.prepareStatement("select c.id, c.descricao from tb_cargo c order by c.id asc;");
        
        ResultSet rs = ps.executeQuery();
         
        listagemRetorno = new ArrayList();
        
        while(rs.next()){
            
            Cargo c = new Cargo();
            c.setId(rs.getInt("id"));
            c.setDescricao(rs.getString("descricao"));
            
            listagemRetorno.add(c);
        }
        
        return listagemRetorno;
    }

    @Override
    public List<Cliente> listCliente() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Equipe> listEquipe() throws Exception {
        
        List<Equipe> listagemRetorno;
        
        //select na tb_equipe
        PreparedStatement ps = 
                this.con.prepareStatement("select id, especialidades, nome "
                                        + "from tb_equipe order by id asc;");
               
        //executa o comando SQL (select)
        ResultSet rs = ps.executeQuery();
        listagemRetorno = new ArrayList();
        while(rs.next()){
            
            Equipe e = new Equipe();
            e.setId(rs.getInt("id"));
            e.setNome(rs.getString("nome"));
            e.setEspecialidades(rs.getString("especialidades"));
            
              PreparedStatement ps2 = 
                this.con.prepareStatement("select equipe_id, funcionario_cpf, nome, cargo from tb_equipe_funcionario, tb_funcionario, tb_pessoa where tb_pessoa.cpf=tb_funcionario.cpf and tb_funcionario.cpf=tb_equipe_funcionario.funcionario_cpf and equipe_id = ? ");
              ps2.setInt(1, e.getId());
              
              ResultSet rs2 = ps2.executeQuery();
              List<Funcionario> listfunc = new ArrayList();
              while(rs2.next()){
                  
                  Funcionario f= new Funcionario();
                  f.setNome(rs2.getString("nome"));
                  Cargo c = new Cargo();
                  c.setId(rs2.getInt("cargo_id"));
                  f.setCargo(c);
                  
                  f.setCpf(rs2.getString("funcionario_cpf"));                
                  listfunc.add(f);
              }
              e.setFuncionarios(listfunc);
              
              rs2.close();
            
            //select equipe_id, funcionario_cpf from tb_equipe_funcionario where equipe_id = 1;
            //recuperar os funcionarios da equipe.
            
            listagemRetorno.add(e);
        }
        rs.close();
        
        return listagemRetorno;
        
        
    }
    
}
