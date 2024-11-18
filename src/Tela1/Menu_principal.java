package Tela1;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.swing.JOptionPane;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template

/**
 *
 * @author pauli
 */
public class Menu_principal extends javax.swing.JFrame {
private List<Item> inventario;
private static final String ARQUIVO_ITENS = "itens.txt";
private static final String ARQUIVO_ID = "ultimo_id.txt";
private Telalogin_1 telaLogin; // Declaração de uma instância da classe Telalogin_1
private double valorDesconto = 0.0;


//SALVANDO ID DOS ITENS
private void salvarIdAtual(int id) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_ID))) {
        writer.write(String.valueOf(id));
        writer.flush();
    } catch (IOException e) {
        e.printStackTrace(); 
    }
}

// Método para obter o próximo ID do itens
private int getProximoId() {
    int id = 1;  // Valor inicial para o ID
    File file = new File(ARQUIVO_ID);

    // Verifica se o arquivo de ID existe
    if (file.exists()) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Lê o último ID armazenado
            String lastId = reader.readLine();
            if (lastId != null) {
                id = Integer.parseInt(lastId) + 1;  // Incrementa o ID
            }
        } catch (IOException e) {
            e.printStackTrace();  // Trate a exceção adequadamente
        }
    }

    return id;
}

///COMBOX VENDA CLEINTE///
private void carregarClientesComboBox() {
    // Limpa os itens atuais do comboBox
    combobox_venda_cliente.removeAllItems();
    
    // Adiciona um item padrão
    combobox_venda_cliente.addItem("Selecione um cliente");

    // Carrega os nomes dos clientes do arquivo
    try (BufferedReader reader = new BufferedReader(new FileReader("clientes.txt"))) {
        String linha;
        while ((linha = reader.readLine()) != null) {
            if (linha.startsWith("Nome:")) {
                String nome = linha.substring(5).trim(); // Extrai o nome do cliente
                combobox_venda_cliente.addItem(nome);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erro ao carregar os clientes.", "Erro", JOptionPane.ERROR_MESSAGE);
    }
}

private void carregarTabelaVendaItens() {
    // Obtém a lista de itens do arquivo
    List<Item> listaDeItens = carregarItensDoArquivo();

    // Configura o modelo da tabela
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0; // Apenas a primeira coluna (checkbox) será editável
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) return Boolean.class; // Checkbox na primeira coluna
            return String.class; // Texto nas demais colunas
        }
    };

    // Adiciona colunas ao modelo
    model.addColumn(""); // Coluna de checkbox sem título
    model.addColumn("ID");
    model.addColumn("Nome");
    model.addColumn("Quantidade");
    model.addColumn("Valor Unitário");

    // Adiciona os itens à tabela
    for (Item item : listaDeItens) {
        model.addRow(new Object[] {
            false, // Checkbox inicializado como não selecionado
            item.getId(),
            item.getNome(),
            item.getQuantidade(),
            item.getValorUnitario()
        });
    }

    // Aplica o modelo à tabela
    venda_tabela_itens.setModel(model);

    // Ajusta a largura da coluna de checkbox
    venda_tabela_itens.getColumnModel().getColumn(0).setMinWidth(20); // Largura mínima
    venda_tabela_itens.getColumnModel().getColumn(0).setMaxWidth(20); // Largura máxima
    venda_tabela_itens.getColumnModel().getColumn(0).setPreferredWidth(20); // Largura preferida

    // Remove o título da coluna de checkbox
    venda_tabela_itens.getColumnModel().getColumn(0).setHeaderValue("");

    // Atualiza o cabeçalho para refletir as mudanças
    venda_tabela_itens.getTableHeader().repaint();
}

private void configurarTabelaListaCompra() {
    // Cria o modelo de tabela
    DefaultTableModel modelListaCompra = new DefaultTableModel();

    // Adiciona as colunas necessárias
    modelListaCompra.addColumn("ID");
    modelListaCompra.addColumn("Nome");
    modelListaCompra.addColumn("Quantidade"); // Coluna para o usuário preencher a quantidade
    modelListaCompra.addColumn("Valor Unitário");

    // Aplica o modelo à tabela
    venda_tabela_listacompra.setModel(modelListaCompra);
}

private void configurarOuvinteTabela() {
    // Obtém o modelo da tabela
    DefaultTableModel model = (DefaultTableModel) venda_tabela_listacompra.getModel();

    // Adiciona um ouvinte ao modelo da tabela
    model.addTableModelListener(e -> {
        int colunaEditada = e.getColumn();
        int linhaEditada = e.getFirstRow();

        // Verifica se a coluna alterada é a coluna de quantidade (índice 2)
        if (colunaEditada == 2) {
            // Atualiza o valor total ao alterar a quantidade
            calcularValorTotal();
        }
    });
}

private void calcularValorTotal() {
    double valorTotal = 0.0; // Variável para armazenar o valor total

    // Obtém o modelo da tabela
    DefaultTableModel model = (DefaultTableModel) venda_tabela_listacompra.getModel();

    // Itera por cada linha da tabela
    for (int i = 0; i < model.getRowCount(); i++) {
        try {
            // Obtém o valor da quantidade (coluna 2) e do valor unitário (coluna 3)
            String quantidadeStr = model.getValueAt(i, 2).toString(); // Pega como String
            int quantidade = Integer.parseInt(quantidadeStr); // Converte para Integer

            double valorUnitario = (double) model.getValueAt(i, 3); // Coluna 3 é o valor unitário

            // Calcula o valor total da linha (quantidade * valor unitário)
            valorTotal += quantidade * valorUnitario;
        } catch (NumberFormatException e) {
            // Se não conseguir converter para Integer, imprime o erro
            System.out.println("Erro ao converter quantidade para número: " + e.getMessage());
        }
    }

    // Atualiza o campo de valor total (JTextField)
    venda_valor.setText(String.format("R$ %.2f", valorTotal));
}

private void inicializarTabelaListaCompra() {
    DefaultTableModel modelListaCompra = new DefaultTableModel();
    modelListaCompra.addColumn("ID");
    modelListaCompra.addColumn("Nome");
    modelListaCompra.addColumn("Quantidade"); // Coluna para o usuário preencher a quantidade
    modelListaCompra.addColumn("Valor Unitário");
    venda_tabela_listacompra.setModel(modelListaCompra);

    // Configura o ouvinte após configurar a tabela
    configurarOuvinteTabela();
}

private void atualizarValorTotal() {
    try {
        // Recupera o valor total da venda formatado
        String valorVendaTexto = venda_valor.getText().replace("R$", "").trim();
        valorVendaTexto = valorVendaTexto.replace(",", ".").replaceAll("[^0-9.]", ""); // Limpa caracteres inválidos
        double valorVenda = Double.parseDouble(valorVendaTexto);

        // Calcula o valor total subtraindo o desconto
        double valorTotal = valorVenda - valorDesconto;

        // Atualiza o campo de valor total
        venda_valor_total.setText(String.format("R$ %.2f", valorTotal));
    } catch (NumberFormatException e) {
        System.out.println("Erro ao calcular o valor total: " + e.getMessage());
    }
}

private void atualizarTabela(List<Item> itens) {
    DefaultTableModel model = (DefaultTableModel) Inventario_tabela.getModel();
    model.setRowCount(0); // Remove todas as linhas existentes

    for (Item item : itens) {
        model.addRow(new Object[]{
            item.getId(),
            item.getNome(),
            item.getData(),
            item.getTipo(),
            item.getMarca(),
            item.getCadastradoPor(),
            item.getQuantidade(),
            item.getValorUnitario(),
            item.getDescricao()
        });
    }
}

private void atualizarValorTotalteste() {
    try {
        // Obtém o valor digitado no campo de valor unitário
        String valorUnitarioTexto = Item_valor_unidade_item.getText().trim();
        double valorUnitario = Double.parseDouble(valorUnitarioTexto);

        // Obtém a quantidade selecionada no JSpinner
        int quantidade = (int) Item_quantidade_item.getValue();

        // Calcula o valor total
        double valorTotal = valorUnitario * quantidade;

        // Atualiza o campo de valor total
        Item_valortotal_item.setText(String.format("%.2f", valorTotal));
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Por favor, insira um valor unitário válido.", "Erro", JOptionPane.ERROR_MESSAGE);
    }
}

// Método auxiliar para extrair o valor após um prefixo
private String extractValue(String linha, String prefixo) {
    if (linha != null && linha.startsWith(prefixo)) {
        return linha.substring(prefixo.length()).trim();
    }
    return ""; // Retorna vazio caso a linha esteja mal formatada
}

private List<Item> listaDeItensOriginal = new ArrayList<>();



    /**
     * Creates new form Menu_principal
     */
    public Menu_principal() {
        initComponents(); 
        carregarItensNaTabela();
        List<Item> inventario = carregarItensDoArquivo(); 
        PreencherTabela preencherTabela = new PreencherTabela(tabelaClientes);
        preencherTabela.carregarDadosDaTabela();
        carregarClientesComboBox();
        carregarTabelaVendaItens();
        configurarTabelaListaCompra();
        inicializarTabelaListaCompra();
        carregarNomesNoComboBox();
        preencherTabelaVendas();
    }

public void atualizarTabelaInventario() {
    List<Item> listaDeItens = carregarItensDoArquivo();  // Carregar os itens do arquivo
    ItemTableModel modeloTabela = new ItemTableModel(listaDeItens);  // Criar o modelo da tabela
    Inventario_tabela.setModel(modeloTabela);  // Definir o modelo da tabela
}

 private String usuarioLogado;

public void setUsuarioLogado(String nomeUsuario) {
        this.usuarioLogado = nomeUsuario;

        // Preencher o campo 'Usuario' com o nome do usuário logado
        Usuario_on.setText(usuarioLogado);  // Preenche o campo (pode ser JTextField ou JTextArea)
  }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Menu_vendas = new javax.swing.JTabbedPane();
        Menu_vendas_tela = new javax.swing.JPanel();
        Menu_vendas_selecao = new javax.swing.JPanel();
        Button_novavenda = new javax.swing.JButton();
        Button_consultarvenda = new javax.swing.JButton();
        Tela_nova_venda = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        venda_endereco = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        Confirmar_venda = new javax.swing.JButton();
        combobox_venda_cliente = new javax.swing.JComboBox<>();
        venda_numcasa = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        venda_tabela_itens = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        venda_tabela_listacompra = new javax.swing.JTable();
        jLabel31 = new javax.swing.JLabel();
        button_adicionar_listacompra = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        Combobox_pagamento = new javax.swing.JComboBox<>();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        venda_valor = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        venda_desconto = new javax.swing.JTextField();
        venda_valor_total = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        BAIRRO = new javax.swing.JLabel();
        venda_bairro = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        venda_contato = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        venda_dataentrega = new javax.swing.JTextField();
        remover_itens = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        Tela_consultar_vendas = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        organiza_clientes_venda = new javax.swing.JButton();
        organiza_entregue_venda = new javax.swing.JButton();
        organiza_pendente_venda = new javax.swing.JButton();
        Pesquisar_venda = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        button_excluir_venda = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        tabela_vendas = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        button_entregar = new javax.swing.JButton();
        Button_limparFiltros = new javax.swing.JButton();
        Menu_clientes_tela = new javax.swing.JPanel();
        Menu_clientes_selecao = new javax.swing.JPanel();
        Button_cadastrarcliente = new javax.swing.JButton();
        Button_consultarclientes = new javax.swing.JButton();
        Tela_novo_cliente = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        Cliente_nome = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        Cliente_endereco = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        Cliente_ncasa = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        Cliente_bairro = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        Cliente_pontoref = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        Cliente_contato = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Cliente_obs = new javax.swing.JTextArea();
        Button_salvar_cliente = new javax.swing.JButton();
        Button_cancelar_cliente = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        Tela_clientes = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        organiza_nome = new javax.swing.JButton();
        organiza_endereco = new javax.swing.JButton();
        organiza_bairro = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        Pesquisar_cliente = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabelaClientes = new javax.swing.JTable();
        excluir_cliente = new javax.swing.JButton();
        Menu_itens_tela = new javax.swing.JPanel();
        Menu_itens_selecao = new javax.swing.JPanel();
        Button_castraritem = new javax.swing.JButton();
        Button_inventario = new javax.swing.JButton();
        Tela_novo_item = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        Item_nome_item = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Item_valor_unidade_item = new javax.swing.JFormattedTextField();
        Item_quantidade_item = new javax.swing.JSpinner();
        Item_marca_item = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        Item_valortotal_item = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        Item_data_item = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        Item_cadastrouser_item = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        Item_descricao_item = new javax.swing.JTextArea();
        jLabel14 = new javax.swing.JLabel();
        Item_tipo_item = new javax.swing.JComboBox<>();
        Item_cancelar_item = new javax.swing.JButton();
        Item_salvar_item = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        Tela_inventario = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Inventario_tabela = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        organiza_item = new javax.swing.JButton();
        organiza_marca = new javax.swing.JButton();
        organiza_tipo = new javax.swing.JButton();
        organiza_data = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        Table_pesquisar = new javax.swing.JTextField();
        Item_delete = new javax.swing.JButton();
        Menu_config = new javax.swing.JPanel();
        Button_editar_usuarios = new javax.swing.JButton();
        Button_desconectar = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        Usuario_on = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel45 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("MENU"); // NOI18N
        getContentPane().setLayout(new java.awt.CardLayout());

        Menu_vendas.setToolTipText("");
        Menu_vendas.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Menu_vendas.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Menu_vendas.setName(""); // NOI18N
        Menu_vendas.setPreferredSize(new java.awt.Dimension(1920, 1080));

        Menu_vendas_tela.setPreferredSize(new java.awt.Dimension(1920, 1080));
        Menu_vendas_tela.setLayout(new java.awt.CardLayout());

        Button_novavenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ICONS/TESTE4.png"))); // NOI18N
        Button_novavenda.setToolTipText("NOVA VENDA");
        Button_novavenda.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Button_novavenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_novavendaActionPerformed(evt);
            }
        });

        Button_consultarvenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ICONS/consultar_vendas.png"))); // NOI18N
        Button_consultarvenda.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Button_consultarvenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_consultarvendaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Menu_vendas_selecaoLayout = new javax.swing.GroupLayout(Menu_vendas_selecao);
        Menu_vendas_selecao.setLayout(Menu_vendas_selecaoLayout);
        Menu_vendas_selecaoLayout.setHorizontalGroup(
            Menu_vendas_selecaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Menu_vendas_selecaoLayout.createSequentialGroup()
                .addContainerGap(656, Short.MAX_VALUE)
                .addComponent(Button_novavenda, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(126, 126, 126)
                .addComponent(Button_consultarvenda, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(648, 648, 648))
        );
        Menu_vendas_selecaoLayout.setVerticalGroup(
            Menu_vendas_selecaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Menu_vendas_selecaoLayout.createSequentialGroup()
                .addGap(349, 349, 349)
                .addGroup(Menu_vendas_selecaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_novavenda, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_consultarvenda, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(451, Short.MAX_VALUE))
        );

        Menu_vendas_tela.add(Menu_vendas_selecao, "card2");

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 0, 22)); // NOI18N
        jLabel1.setText("NOVA VENDA");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel2.setText("CLIENTE:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel3.setText("ENDEREÇO:");

        venda_endereco.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        venda_endereco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                venda_enderecoActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel4.setText("N° CASA:");

        jButton1.setBackground(new java.awt.Color(255, 51, 51));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("VOLTAR");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        Confirmar_venda.setBackground(new java.awt.Color(51, 204, 0));
        Confirmar_venda.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Confirmar_venda.setForeground(new java.awt.Color(255, 255, 255));
        Confirmar_venda.setText("CONFIRMAR");
        Confirmar_venda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Confirmar_vendaActionPerformed(evt);
            }
        });

        combobox_venda_cliente.setEditable(true);
        combobox_venda_cliente.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        combobox_venda_cliente.setMaximumRowCount(10000);
        combobox_venda_cliente.setToolTipText("");
        combobox_venda_cliente.setAutoscrolls(true);
        combobox_venda_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox_venda_clienteActionPerformed(evt);
            }
        });

        venda_numcasa.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        venda_numcasa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                venda_numcasaActionPerformed(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        jLabel30.setText("ITENS:");

        venda_tabela_itens.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        venda_tabela_itens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        venda_tabela_itens.setCellSelectionEnabled(true);
        venda_tabela_itens.setRowHeight(30);
        jScrollPane4.setViewportView(venda_tabela_itens);
        venda_tabela_itens.getAccessibleContext().setAccessibleName("venda_tabela_itens");

        venda_tabela_listacompra.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        venda_tabela_listacompra.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        venda_tabela_listacompra.setRowHeight(30);
        jScrollPane6.setViewportView(venda_tabela_listacompra);
        venda_tabela_listacompra.getAccessibleContext().setAccessibleName("venda_tabela_listacompra");

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        jLabel31.setText("LISTA DE COMPRA:");

        button_adicionar_listacompra.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_adicionar_listacompra.setText("ADICIONAR");
        button_adicionar_listacompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_adicionar_listacompraActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 0, 51));
        jLabel32.setText(">>>");

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        jLabel33.setText("VALORES:");

        Combobox_pagamento.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Combobox_pagamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DINHEIRO", "PIX", "CARTÃO DÉBITO", "CARTÃO CRÉDITO" }));
        Combobox_pagamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Combobox_pagamentoActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel34.setText("FORMA DE PAGAMENTO:");

        jLabel35.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel35.setText("VALOR:");

        venda_valor.setEditable(false);
        venda_valor.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        venda_valor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        venda_valor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                venda_valorActionPerformed(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel36.setText("DESCONTO:");

        venda_desconto.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        venda_desconto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        venda_desconto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                venda_descontoActionPerformed(evt);
            }
        });

        venda_valor_total.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        venda_valor_total.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        venda_valor_total.setToolTipText("");
        venda_valor_total.setOpaque(true);
        venda_valor_total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                venda_valor_totalActionPerformed(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        jLabel37.setText("VALOR TOTAL:");

        BAIRRO.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        BAIRRO.setText("BAIRRO:");

        venda_bairro.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        venda_bairro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                venda_bairroActionPerformed(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel38.setText("CONTATO:");

        venda_contato.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        venda_contato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                venda_contatoActionPerformed(evt);
            }
        });

        jLabel39.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel39.setText("DATA P/ ENTREGA:");

        venda_dataentrega.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        venda_dataentrega.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        venda_dataentrega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                venda_dataentregaActionPerformed(evt);
            }
        });

        remover_itens.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        remover_itens.setText("REMOVER");
        remover_itens.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remover_itensActionPerformed(evt);
            }
        });

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 0, 51));
        jLabel40.setText("<<<");

        jLabel44.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(255, 51, 51));
        jLabel44.setText("DIGITE A QUANTIDADE DE ITENS NA TABELA*");

        javax.swing.GroupLayout Tela_nova_vendaLayout = new javax.swing.GroupLayout(Tela_nova_venda);
        Tela_nova_venda.setLayout(Tela_nova_vendaLayout);
        Tela_nova_vendaLayout.setHorizontalGroup(
            Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3)
            .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(combobox_venda_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(45, 45, 45)
                        .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(venda_endereco, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(45, 45, 45)
                        .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(venda_numcasa))
                        .addGap(45, 45, 45)
                        .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BAIRRO)
                            .addComponent(venda_bairro, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37)
                        .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38)
                            .addComponent(venda_contato, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(75, 75, 75)
                        .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(venda_dataentrega))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_nova_vendaLayout.createSequentialGroup()
                        .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                                .addComponent(Confirmar_venda)
                                .addGap(39, 39, 39)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                                .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel30)
                                    .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(button_adicionar_listacompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(remover_itens, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                            .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                                                .addGap(48, 48, 48)
                                                .addComponent(jLabel32))
                                            .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                                                .addGap(47, 47, 47)
                                                .addComponent(jLabel40)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                                        .addComponent(jLabel31)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel44))
                                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(56, 56, 56)
                                .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Combobox_pagamento, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(venda_valor)
                                    .addComponent(venda_desconto)
                                    .addComponent(venda_valor_total)
                                    .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                                        .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel33)
                                            .addComponent(jLabel34)
                                            .addComponent(jLabel35)
                                            .addComponent(jLabel36)
                                            .addComponent(jLabel37))
                                        .addGap(0, 124, Short.MAX_VALUE)))))
                        .addContainerGap(106, Short.MAX_VALUE))))
            .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        Tela_nova_vendaLayout.setVerticalGroup(
            Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_nova_vendaLayout.createSequentialGroup()
                .addContainerGap(43, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(BAIRRO)
                    .addComponent(jLabel38)
                    .addComponent(jLabel39))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(combobox_venda_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(venda_endereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(venda_numcasa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(venda_bairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(venda_contato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(venda_dataentrega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Combobox_pagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(venda_valor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(venda_desconto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(243, 243, 243)
                        .addComponent(jLabel37)
                        .addGap(18, 18, 18)
                        .addComponent(venda_valor_total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                        .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel44)
                            .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel30)
                                .addComponent(jLabel31)))
                        .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                                .addGap(221, 221, 221)
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_adicionar_listacompra)
                                .addGap(18, 18, 18)
                                .addComponent(remover_itens)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel40))
                            .addGroup(Tela_nova_vendaLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(Tela_nova_vendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Confirmar_venda, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(88, 88, 88))
        );

        Menu_vendas_tela.add(Tela_nova_venda, "card3");

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel41.setText("CONSULTAR VENDAS");

        jLabel42.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel42.setText("ORGANIZAR POR:");

        organiza_clientes_venda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        organiza_clientes_venda.setText("CLIENTE");
        organiza_clientes_venda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                organiza_clientes_vendaActionPerformed(evt);
            }
        });

        organiza_entregue_venda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        organiza_entregue_venda.setText("ENTREGUES");
        organiza_entregue_venda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                organiza_entregue_vendaActionPerformed(evt);
            }
        });

        organiza_pendente_venda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        organiza_pendente_venda.setText("PENDENTES");
        organiza_pendente_venda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                organiza_pendente_vendaActionPerformed(evt);
            }
        });

        Pesquisar_venda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Pesquisar_venda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Pesquisar_vendaActionPerformed(evt);
            }
        });

        jLabel43.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel43.setText("PESQUISAR:");

        button_excluir_venda.setBackground(new java.awt.Color(255, 51, 51));
        button_excluir_venda.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        button_excluir_venda.setText("EXCLUIR");
        button_excluir_venda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_excluir_vendaActionPerformed(evt);
            }
        });

        tabela_vendas.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tabela_vendas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabela_vendas.setRowHeight(30);
        tabela_vendas.setShowHorizontalLines(true);
        jScrollPane7.setViewportView(tabela_vendas);
        tabela_vendas.getAccessibleContext().setAccessibleName("tabela_vendas");

        jButton8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton8.setText("VOLTAR");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        button_entregar.setBackground(new java.awt.Color(51, 204, 0));
        button_entregar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        button_entregar.setText("ENTREGAR");
        button_entregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_entregarActionPerformed(evt);
            }
        });

        Button_limparFiltros.setBackground(new java.awt.Color(153, 102, 255));
        Button_limparFiltros.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Button_limparFiltros.setText("LIMPAR FILTROS");
        Button_limparFiltros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_limparFiltrosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Tela_consultar_vendasLayout = new javax.swing.GroupLayout(Tela_consultar_vendas);
        Tela_consultar_vendas.setLayout(Tela_consultar_vendasLayout);
        Tela_consultar_vendasLayout.setHorizontalGroup(
            Tela_consultar_vendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Tela_consultar_vendasLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(Tela_consultar_vendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Tela_consultar_vendasLayout.createSequentialGroup()
                        .addGroup(Tela_consultar_vendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(Tela_consultar_vendasLayout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addComponent(jLabel42)
                                .addGap(18, 18, 18)
                                .addComponent(organiza_clientes_venda)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(organiza_entregue_venda)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(organiza_pendente_venda)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Button_limparFiltros)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 466, Short.MAX_VALUE)
                                .addComponent(button_entregar)
                                .addGap(18, 18, 18)
                                .addComponent(button_excluir_venda)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel43)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Pesquisar_venda, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(36, 36, 36))
                    .addGroup(Tela_consultar_vendasLayout.createSequentialGroup()
                        .addComponent(jLabel41)
                        .addContainerGap(1645, Short.MAX_VALUE))))
        );
        Tela_consultar_vendasLayout.setVerticalGroup(
            Tela_consultar_vendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Tela_consultar_vendasLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel41)
                .addGap(55, 55, 55)
                .addGroup(Tela_consultar_vendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42)
                    .addGroup(Tela_consultar_vendasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(organiza_clientes_venda)
                        .addComponent(organiza_entregue_venda)
                        .addComponent(organiza_pendente_venda)
                        .addComponent(Pesquisar_venda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel43)
                        .addComponent(button_excluir_venda)
                        .addComponent(button_entregar)
                        .addComponent(Button_limparFiltros)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 720, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );

        Menu_vendas_tela.add(Tela_consultar_vendas, "card4");

        Menu_vendas.addTab("VENDAS", Menu_vendas_tela);

        Menu_clientes_tela.setPreferredSize(new java.awt.Dimension(1920, 1080));
        Menu_clientes_tela.setLayout(new java.awt.CardLayout());

        Button_cadastrarcliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ICONS/CADASTRAr_clientes.png"))); // NOI18N
        Button_cadastrarcliente.setToolTipText("NOVA VENDA");
        Button_cadastrarcliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Button_cadastrarcliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_cadastrarclienteActionPerformed(evt);
            }
        });

        Button_consultarclientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ICONS/consultar_clienteS.png"))); // NOI18N
        Button_consultarclientes.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Button_consultarclientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_consultarclientesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Menu_clientes_selecaoLayout = new javax.swing.GroupLayout(Menu_clientes_selecao);
        Menu_clientes_selecao.setLayout(Menu_clientes_selecaoLayout);
        Menu_clientes_selecaoLayout.setHorizontalGroup(
            Menu_clientes_selecaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Menu_clientes_selecaoLayout.createSequentialGroup()
                .addContainerGap(656, Short.MAX_VALUE)
                .addComponent(Button_cadastrarcliente, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(126, 126, 126)
                .addComponent(Button_consultarclientes, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(648, 648, 648))
        );
        Menu_clientes_selecaoLayout.setVerticalGroup(
            Menu_clientes_selecaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Menu_clientes_selecaoLayout.createSequentialGroup()
                .addGap(349, 349, 349)
                .addGroup(Menu_clientes_selecaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_cadastrarcliente, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_consultarclientes, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(451, Short.MAX_VALUE))
        );

        Menu_clientes_tela.add(Menu_clientes_selecao, "card4");

        jLabel18.setBackground(new java.awt.Color(255, 51, 51));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setText("    DADOS:");
        jLabel18.setToolTipText("");
        jLabel18.setDoubleBuffered(true);

        Cliente_nome.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Cliente_nome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cliente_nomeActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel19.setText("NOME:");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel20.setText("ENDEREÇO:");

        Cliente_endereco.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel21.setText("N° CASA:");

        Cliente_ncasa.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Cliente_ncasa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cliente_ncasaActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel22.setText("BAIRRO:");

        Cliente_bairro.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Cliente_bairro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cliente_bairroActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel23.setText("PONTO DE REFERÊNCIA:");

        Cliente_pontoref.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel24.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel24.setText("CONTATO:");

        Cliente_contato.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel25.setText("OBSERVAÇÕES:");

        jScrollPane3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        Cliente_obs.setColumns(20);
        Cliente_obs.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Cliente_obs.setRows(5);
        Cliente_obs.setDragEnabled(true);
        jScrollPane3.setViewportView(Cliente_obs);

        Button_salvar_cliente.setBackground(new java.awt.Color(51, 204, 0));
        Button_salvar_cliente.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        Button_salvar_cliente.setText("SALVAR");
        Button_salvar_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_salvar_clienteActionPerformed(evt);
            }
        });

        Button_cancelar_cliente.setBackground(new java.awt.Color(255, 0, 0));
        Button_cancelar_cliente.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        Button_cancelar_cliente.setText("CANCELAR");
        Button_cancelar_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_cancelar_clienteActionPerformed(evt);
            }
        });

        jLabel26.setBackground(new java.awt.Color(255, 51, 51));
        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel26.setText("   ENDEREÇO:");

        javax.swing.GroupLayout Tela_novo_clienteLayout = new javax.swing.GroupLayout(Tela_novo_cliente);
        Tela_novo_cliente.setLayout(Tela_novo_clienteLayout);
        Tela_novo_clienteLayout.setHorizontalGroup(
            Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_novo_clienteLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(Button_salvar_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(Button_cancelar_cliente)
                .addGap(117, 117, 117))
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(Tela_novo_clienteLayout.createSequentialGroup()
                .addGroup(Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Tela_novo_clienteLayout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(Cliente_nome, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24)
                            .addComponent(Cliente_contato, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(Tela_novo_clienteLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 1777, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Tela_novo_clienteLayout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addGroup(Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Tela_novo_clienteLayout.createSequentialGroup()
                                .addGroup(Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Cliente_endereco, javax.swing.GroupLayout.PREFERRED_SIZE, 381, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel25))
                                .addGap(43, 43, 43)
                                .addGroup(Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Cliente_ncasa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel21))
                                .addGap(45, 45, 45)
                                .addGroup(Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22)
                                    .addComponent(Cliente_bairro, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(44, 44, 44)
                                .addGroup(Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Cliente_pontoref, javax.swing.GroupLayout.PREFERRED_SIZE, 381, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel23)))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1194, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(Tela_novo_clienteLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 1833, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(54, Short.MAX_VALUE))
        );
        Tela_novo_clienteLayout.setVerticalGroup(
            Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Tela_novo_clienteLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Cliente_nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Cliente_contato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addGroup(Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jLabel21)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Cliente_endereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Cliente_ncasa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(Cliente_pontoref, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(Cliente_bairro)))
                .addGap(18, 18, 18)
                .addComponent(jLabel25)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE)
                .addGroup(Tela_novo_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_cancelar_cliente)
                    .addComponent(Button_salvar_cliente))
                .addGap(98, 98, 98))
        );

        Menu_clientes_tela.add(Tela_novo_cliente, "Tela_novo_cliente");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel27.setText("CLIENTES");

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel28.setText("ORGANIZAR POR:");

        organiza_nome.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        organiza_nome.setText("NOME");
        organiza_nome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                organiza_nomeActionPerformed(evt);
            }
        });

        organiza_endereco.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        organiza_endereco.setText("ENDEREÇO");
        organiza_endereco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                organiza_enderecoActionPerformed(evt);
            }
        });

        organiza_bairro.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        organiza_bairro.setText("BAIRRO");
        organiza_bairro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                organiza_bairroActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton9.setText("VOLTAR");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        Pesquisar_cliente.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Pesquisar_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Pesquisar_clienteActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel29.setText("PESQUISAR:");

        tabelaClientes.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tabelaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOME", "CONTATO", "ENDEREÇO", "BAIRRO", "DATA", "CADASTRADO POR"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelaClientes.setToolTipText("");
        tabelaClientes.setRowHeight(30);
        tabelaClientes.setShowHorizontalLines(true);
        jScrollPane5.setViewportView(tabelaClientes);
        tabelaClientes.getAccessibleContext().setAccessibleName("tabelaClientes");

        excluir_cliente.setBackground(new java.awt.Color(255, 51, 51));
        excluir_cliente.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        excluir_cliente.setText("EXCLUIR");
        excluir_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excluir_clienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Tela_clientesLayout = new javax.swing.GroupLayout(Tela_clientes);
        Tela_clientes.setLayout(Tela_clientesLayout);
        Tela_clientesLayout.setHorizontalGroup(
            Tela_clientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Tela_clientesLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel27)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_clientesLayout.createSequentialGroup()
                .addGroup(Tela_clientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, Tela_clientesLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabel28)
                        .addGap(18, 18, 18)
                        .addComponent(organiza_nome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(organiza_endereco)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(organiza_bairro)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(excluir_cliente)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Pesquisar_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Tela_clientesLayout.createSequentialGroup()
                        .addGap(0, 44, Short.MAX_VALUE)
                        .addGroup(Tela_clientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1828, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(48, 48, 48))
        );
        Tela_clientesLayout.setVerticalGroup(
            Tela_clientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Tela_clientesLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel27)
                .addGap(55, 55, 55)
                .addGroup(Tela_clientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28)
                    .addGroup(Tela_clientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(organiza_nome)
                        .addComponent(organiza_endereco)
                        .addComponent(organiza_bairro)
                        .addComponent(Pesquisar_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel29)
                        .addComponent(excluir_cliente)))
                .addGap(24, 24, 24)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 699, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        Menu_clientes_tela.add(Tela_clientes, "card4");

        Menu_vendas.addTab("CLIENTES", Menu_clientes_tela);

        Menu_itens_tela.setPreferredSize(new java.awt.Dimension(1920, 1080));
        Menu_itens_tela.setLayout(new java.awt.CardLayout());

        Button_castraritem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ICONS/CADASTRAR ITEM.png"))); // NOI18N
        Button_castraritem.setToolTipText("NOVA VENDA");
        Button_castraritem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Button_castraritem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_castraritemActionPerformed(evt);
            }
        });

        Button_inventario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ICONS/inventario.png"))); // NOI18N
        Button_inventario.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Button_inventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_inventarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Menu_itens_selecaoLayout = new javax.swing.GroupLayout(Menu_itens_selecao);
        Menu_itens_selecao.setLayout(Menu_itens_selecaoLayout);
        Menu_itens_selecaoLayout.setHorizontalGroup(
            Menu_itens_selecaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Menu_itens_selecaoLayout.createSequentialGroup()
                .addContainerGap(656, Short.MAX_VALUE)
                .addComponent(Button_castraritem, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(126, 126, 126)
                .addComponent(Button_inventario, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(648, 648, 648))
        );
        Menu_itens_selecaoLayout.setVerticalGroup(
            Menu_itens_selecaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Menu_itens_selecaoLayout.createSequentialGroup()
                .addGap(349, 349, 349)
                .addGroup(Menu_itens_selecaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_castraritem, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_inventario, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(451, Short.MAX_VALUE))
        );

        Menu_itens_tela.add(Menu_itens_selecao, "card4");

        jLabel5.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel5.setText("NOVO ITEM");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel6.setText("NOME DO PRODUTO:");

        Item_nome_item.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Item_nome_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_nome_itemActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel7.setText("QUANTIDADE:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel8.setText("MARCA:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel9.setText("VALOR UNIDADE:");

        Item_valor_unidade_item.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        Item_valor_unidade_item.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        Item_valor_unidade_item.setActionCommand("<Not Set>");
        Item_valor_unidade_item.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Item_valor_unidade_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_valor_unidade_itemActionPerformed(evt);
            }
        });

        Item_quantidade_item.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        Item_marca_item.setEditable(true);
        Item_marca_item.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Item_marca_item.setMaximumRowCount(500);
        Item_marca_item.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        Item_marca_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_marca_itemActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel10.setText("VALOR TOTAL:");

        Item_valortotal_item.setEditable(false);
        Item_valortotal_item.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Item_valortotal_item.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        Item_valortotal_item.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Item_valortotal_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_valortotal_itemActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel11.setText("DATA ENTRADA:");

        Item_data_item.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Item_data_item.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Item_data_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_data_itemActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel12.setText("DESCRIÇÃO DO PRODUTO:");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel13.setText("CADASTRADO POR:");

        Item_cadastrouser_item.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Item_cadastrouser_item.setMaximumRowCount(5000);
        Item_cadastrouser_item.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AVULSO" }));
        Item_cadastrouser_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_cadastrouser_itemActionPerformed(evt);
            }
        });

        Item_descricao_item.setColumns(20);
        Item_descricao_item.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Item_descricao_item.setRows(5);
        jScrollPane1.setViewportView(Item_descricao_item);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel14.setText("TIPO:");

        Item_tipo_item.setEditable(true);
        Item_tipo_item.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Item_tipo_item.setMaximumRowCount(1000);
        Item_tipo_item.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "UNIDADE", "PACOTE", "FARDO", "MILHAR" }));
        Item_tipo_item.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        Item_cancelar_item.setBackground(new java.awt.Color(255, 0, 0));
        Item_cancelar_item.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Item_cancelar_item.setText("VOLTAR");
        Item_cancelar_item.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Item_cancelar_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_cancelar_itemActionPerformed(evt);
            }
        });

        Item_salvar_item.setBackground(new java.awt.Color(102, 204, 0));
        Item_salvar_item.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Item_salvar_item.setText("SALVAR");
        Item_salvar_item.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Item_salvar_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_salvar_itemActionPerformed(evt);
            }
        });

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout Tela_novo_itemLayout = new javax.swing.GroupLayout(Tela_novo_item);
        Tela_novo_item.setLayout(Tela_novo_itemLayout);
        Tela_novo_itemLayout.setHorizontalGroup(
            Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_novo_itemLayout.createSequentialGroup()
                .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Tela_novo_itemLayout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Tela_novo_itemLayout.createSequentialGroup()
                                .addComponent(jScrollPane1)
                                .addGap(92, 92, 92))
                            .addGroup(Tela_novo_itemLayout.createSequentialGroup()
                                .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addGroup(Tela_novo_itemLayout.createSequentialGroup()
                                        .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel6)
                                            .addComponent(Item_nome_item, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel8)
                                            .addComponent(Item_marca_item, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(56, 56, 56)
                                        .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Item_tipo_item, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel14))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 945, Short.MAX_VALUE))))
                    .addGroup(Tela_novo_itemLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(Item_data_item, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel7)
                        .addComponent(jLabel9)
                        .addGroup(Tela_novo_itemLayout.createSequentialGroup()
                            .addComponent(Item_salvar_item, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(48, 48, 48)
                            .addComponent(Item_cancelar_item, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel10)
                        .addComponent(Item_valortotal_item)
                        .addComponent(Item_valor_unidade_item)
                        .addComponent(Item_quantidade_item)
                        .addComponent(Item_cadastrouser_item, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel13))
                .addGap(105, 105, 105))
        );
        Tela_novo_itemLayout.setVerticalGroup(
            Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_novo_itemLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_novo_itemLayout.createSequentialGroup()
                        .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Item_nome_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Item_tipo_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_novo_itemLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Item_data_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)))
                .addGap(18, 18, 18)
                .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Tela_novo_itemLayout.createSequentialGroup()
                        .addComponent(Item_cadastrouser_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7))
                    .addGroup(Tela_novo_itemLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Item_marca_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Tela_novo_itemLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12))
                    .addGroup(Tela_novo_itemLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(Item_quantidade_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_novo_itemLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(Item_valor_unidade_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(Item_valortotal_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                .addGroup(Tela_novo_itemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Item_salvar_item, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Item_cancelar_item, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(78, Short.MAX_VALUE))
            .addComponent(jSeparator5)
        );

        Menu_itens_tela.add(Tela_novo_item, "card3");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel15.setText("INVENTARIO");

        Inventario_tabela.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Inventario_tabela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "ITEM", "MARCA", "TIPO", "CADASTRADO POR", "DATA", "QUANTIDADE", "VALOR"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Inventario_tabela.setColumnSelectionAllowed(true);
        Inventario_tabela.setRowHeight(30);
        Inventario_tabela.setShowGrid(false);
        Inventario_tabela.setShowHorizontalLines(true);
        jScrollPane2.setViewportView(Inventario_tabela);
        Inventario_tabela.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (Inventario_tabela.getColumnModel().getColumnCount() > 0) {
            Inventario_tabela.getColumnModel().getColumn(0).setMaxWidth(100);
            Inventario_tabela.getColumnModel().getColumn(2).setHeaderValue("MARCA");
            Inventario_tabela.getColumnModel().getColumn(3).setHeaderValue("TIPO");
            Inventario_tabela.getColumnModel().getColumn(6).setHeaderValue("QUANTIDADE");
            Inventario_tabela.getColumnModel().getColumn(7).setHeaderValue("VALOR");
        }

        jButton3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton3.setText("VOLTAR");
        jButton3.setToolTipText("");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setText("ORGANIZAR POR:");

        organiza_item.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        organiza_item.setText("ITEM");
        organiza_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                organiza_itemActionPerformed(evt);
            }
        });

        organiza_marca.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        organiza_marca.setText("MARCA");
        organiza_marca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                organiza_marcaActionPerformed(evt);
            }
        });

        organiza_tipo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        organiza_tipo.setText("TIPO");
        organiza_tipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                organiza_tipoActionPerformed(evt);
            }
        });

        organiza_data.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        organiza_data.setText("DATA");
        organiza_data.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                organiza_dataActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel17.setText("PESQUISAR:");

        Table_pesquisar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Table_pesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Table_pesquisarActionPerformed(evt);
            }
        });

        Item_delete.setBackground(new java.awt.Color(255, 51, 51));
        Item_delete.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Item_delete.setText("EXCLUIR");
        Item_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_deleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Tela_inventarioLayout = new javax.swing.GroupLayout(Tela_inventario);
        Tela_inventario.setLayout(Tela_inventarioLayout);
        Tela_inventarioLayout.setHorizontalGroup(
            Tela_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Tela_inventarioLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(Tela_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(Tela_inventarioLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(1695, 1695, 1695))
                    .addGroup(Tela_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(Tela_inventarioLayout.createSequentialGroup()
                            .addComponent(jLabel16)
                            .addGap(18, 18, 18)
                            .addComponent(organiza_item)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(organiza_marca)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(organiza_tipo)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(organiza_data)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Item_delete)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel17)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(Table_pesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1829, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        Tela_inventarioLayout.setVerticalGroup(
            Tela_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Tela_inventarioLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel15)
                .addGap(55, 55, 55)
                .addGroup(Tela_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(organiza_item)
                    .addComponent(organiza_marca)
                    .addComponent(organiza_tipo)
                    .addComponent(organiza_data)
                    .addComponent(jLabel17)
                    .addComponent(Table_pesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Item_delete))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 717, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Menu_itens_tela.add(Tela_inventario, "card4");

        Menu_vendas.addTab("INVENTÁRIO", Menu_itens_tela);

        Button_editar_usuarios.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        Button_editar_usuarios.setText("EDITAR USUARIOS");
        Button_editar_usuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_editar_usuariosActionPerformed(evt);
            }
        });

        Button_desconectar.setBackground(new java.awt.Color(255, 51, 51));
        Button_desconectar.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        Button_desconectar.setText("DESCONECTAR");
        Button_desconectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_desconectarActionPerformed(evt);
            }
        });

        jLabel46.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jLabel46.setText("USUARIO ATUAL:");

        Usuario_on.setEditable(false);
        Usuario_on.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        Usuario_on.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Usuario_on.setBorder(null);
        Usuario_on.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Usuario_on.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        Usuario_on.setEnabled(false);
        Usuario_on.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Usuario_onActionPerformed(evt);
            }
        });

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel45.setIcon(new javax.swing.ImageIcon("C:\\Users\\pauli\\Downloads\\icons java\\ICON.png")); // NOI18N

        javax.swing.GroupLayout Menu_configLayout = new javax.swing.GroupLayout(Menu_config);
        Menu_config.setLayout(Menu_configLayout);
        Menu_configLayout.setHorizontalGroup(
            Menu_configLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Menu_configLayout.createSequentialGroup()
                .addGroup(Menu_configLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Menu_configLayout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(Menu_configLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Usuario_on)
                            .addComponent(Button_editar_usuarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Button_desconectar, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(Menu_configLayout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(jLabel46))))
                    .addGroup(Menu_configLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel45)))
                .addGap(28, 28, 28)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1522, Short.MAX_VALUE))
        );
        Menu_configLayout.setVerticalGroup(
            Menu_configLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Menu_configLayout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(jLabel46)
                .addGap(18, 18, 18)
                .addComponent(Usuario_on, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67)
                .addComponent(Button_editar_usuarios)
                .addGap(51, 51, 51)
                .addComponent(Button_desconectar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 583, Short.MAX_VALUE)
                .addComponent(jLabel45)
                .addGap(33, 33, 33))
            .addGroup(Menu_configLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator4)
                .addContainerGap())
        );

        Menu_vendas.addTab("CONFIGURAÇÕES", Menu_config);

        getContentPane().add(Menu_vendas, "Tab_menuprincipal");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Button_consultarvendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_consultarvendaActionPerformed
    Menu_vendas_selecao.setVisible(false);
    //pfvr de certo
    Tela_consultar_vendas.setVisible(true);
    
    }//GEN-LAST:event_Button_consultarvendaActionPerformed

    private void Button_novavendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_novavendaActionPerformed
    //teste louco
    carregarTabelaVendaItens();
    Menu_vendas_selecao.setVisible(false);
    //pfvr de certo
    Tela_nova_venda.setVisible(true);
    }//GEN-LAST:event_Button_novavendaActionPerformed

    private void Button_cadastrarclienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_cadastrarclienteActionPerformed
    Menu_clientes_selecao.setVisible(false);
    //pfvr de certo
    Tela_novo_cliente.setVisible(true);
    }//GEN-LAST:event_Button_cadastrarclienteActionPerformed

    private void Button_consultarclientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_consultarclientesActionPerformed
    Menu_clientes_selecao.setVisible(false);
    Tela_clientes.setVisible(true);
    PreencherTabela preencherTabela = new PreencherTabela(tabelaClientes); // Passa a JTable
    preencherTabela.carregarDadosDaTabela(); 
    }//GEN-LAST:event_Button_consultarclientesActionPerformed

    private void Button_castraritemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_castraritemActionPerformed

    Menu_itens_selecao.setVisible(false);
    Tela_novo_item.setVisible(true);
    }//GEN-LAST:event_Button_castraritemActionPerformed

    private void Button_inventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_inventarioActionPerformed
    Menu_itens_selecao.setVisible(false);
    Tela_inventario.setVisible(true);
    }//GEN-LAST:event_Button_inventarioActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Obtém o modelo da tabela
    DefaultTableModel model = (DefaultTableModel) venda_tabela_listacompra.getModel();

    // Limpa todos os dados da tabela
    model.setRowCount(0);

    // Atualiza o valor total para 0
    venda_valor.setText("R$ 0.00");

    // Atualiza o valor total com desconto para 0
    venda_valor_total.setText("R$ 0.00");
    
    Tela_nova_venda.setVisible(false);
    Menu_vendas_selecao.setVisible(true);
    
    preencherTabelaVendas();
    
    }//GEN-LAST:event_jButton1ActionPerformed

    private void combobox_venda_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_venda_clienteActionPerformed
    String clienteSelecionado = (String) combobox_venda_cliente.getSelectedItem();
    if (clienteSelecionado != null && !clienteSelecionado.equals("Selecione um cliente")) {
        try (BufferedReader reader = new BufferedReader(new FileReader("clientes.txt"))) {
            String linha;
            boolean clienteEncontrado = false;

            while ((linha = reader.readLine()) != null) {
                if (linha.startsWith("Nome:") && linha.substring(5).trim().equals(clienteSelecionado)) {
                    clienteEncontrado = true;

                    // Leia as próximas linhas para pegar as informações
                    String contato = extractValue(reader.readLine(), "Contato:");
                    String endereco = extractValue(reader.readLine(), "Endereço:");
                    String numero = extractValue(reader.readLine(), "Número:");
                    String bairro = extractValue(reader.readLine(), "Bairro:");

                    // Preenche os campos de texto
                    venda_contato.setText(contato);
                    venda_endereco.setText(endereco);
                    venda_numcasa.setText(numero);
                    venda_bairro.setText(bairro);

                    break; // Para a busca, já encontramos o cliente
                }
            }

            if (!clienteEncontrado) {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado no arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar informações do cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    }//GEN-LAST:event_combobox_venda_clienteActionPerformed

    private void Item_data_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_data_itemActionPerformed
        String texto = Item_data_item.getText();

        // Verifique se o texto tem o tamanho correto para uma data (8 caracteres: ddMMyyyy)
        if (texto.length() == 8) {
            // Adicione a formatação de data: "dd/MM/yyyy"
            texto = texto.substring(0, 2) + "/" + texto.substring(2, 4) + "/" + texto.substring(4);

            // Defina o texto formatado de volta ao jTextField
            Item_data_item.setText(texto);
        } else {
            // Se o texto não tiver 8 caracteres, pode exibir uma mensagem de erro ou limpar o campo
            JOptionPane.showMessageDialog(this, "Digite a data no formato ddMMyyyy!");
        }
    }//GEN-LAST:event_Item_data_itemActionPerformed

    private void Item_marca_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_marca_itemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Item_marca_itemActionPerformed

    private void Item_valor_unidade_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_valor_unidade_itemActionPerformed
    
    atualizarValorTotalteste();        // TODO add your handling code here:
    }//GEN-LAST:event_Item_valor_unidade_itemActionPerformed

    private void Item_nome_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_nome_itemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Item_nome_itemActionPerformed

    private void Confirmar_vendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Confirmar_vendaActionPerformed
// Caminho do arquivo para guardar o último ID da venda
    String arquivoUltimoIDVenda = "ultimoIDVenda.txt";
    int proximoIDVenda;

    // Tentar ler o último ID da venda do arquivo
    try (BufferedReader reader = new BufferedReader(new FileReader(arquivoUltimoIDVenda))) {
        proximoIDVenda = Integer.parseInt(reader.readLine());
    } catch (IOException | NumberFormatException e) {
        proximoIDVenda = 1;  // Começar do ID 1 se não encontrar
    }

    // Coletando os dados dos campos
    String nomeCliente = combobox_venda_cliente.getSelectedItem().toString().trim();
    String endereco = venda_endereco.getText().trim();
    String numeroCasa = venda_numcasa.getText().trim();
    String bairro = venda_bairro.getText().trim();
    String contato = venda_contato.getText().trim();
    String dataEntrega = venda_dataentrega.getText().trim();
    String metodoPagamento = Combobox_pagamento.getSelectedItem().toString().trim();
    String valorTotal = venda_valor_total.getText().trim();

    // Montar os dados da venda para salvar
    String dadosVenda = String.format(
        "Venda ID: %d\nCliente: %s\nEndereço: %s\nNúmero: %s\nBairro: %s\nContato: %s\nData de Entrega: %s\nPagamento: %s\nValor Total: %s\nEstado: Pendente\n\n",
        proximoIDVenda, nomeCliente, endereco, numeroCasa, bairro, contato, dataEntrega, metodoPagamento, valorTotal
    );

    // Salvar no arquivo de vendas
    try (FileWriter writer = new FileWriter("Vendas.txt", true)) {
        writer.write(dadosVenda);
        writer.flush();

        // Exibir mensagem de sucesso
        JOptionPane.showMessageDialog(null, "Venda salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException e) {
        e.printStackTrace();
        // Exibir mensagem de erro
        JOptionPane.showMessageDialog(null, "Erro ao salvar a venda. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // Atualizar o arquivo de último ID usado para a próxima venda
    proximoIDVenda++;  // Incrementa o ID
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoUltimoIDVenda))) {
        writer.write(String.valueOf(proximoIDVenda));  // Salva o próximo ID
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Salvar os dados dos itens da venda
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("Vendas.txt", true))) {
        // Aqui vamos percorrer as linhas da tabela venda_tabela_listacompra e salvar os dados
        DefaultTableModel model = (DefaultTableModel) venda_tabela_listacompra.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            // Ajuste para corresponder ao número correto de colunas na sua tabela
            String produto = model.getValueAt(i, 1).toString();  // Produto (coluna 1)
            String quantidade = model.getValueAt(i, 2).toString();  // Quantidade (coluna 2)
            String valorUnitario = model.getValueAt(i, 3).toString();  // Valor unitário (coluna 3)

            // Escreve os dados do item no arquivo
            writer.write(String.format("Produto: %s | Quantidade: %s | Valor Unitário: %s\n", 
                produto, quantidade, valorUnitario));
        }
        writer.flush();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }//GEN-LAST:event_Confirmar_vendaActionPerformed

    private void Item_cancelar_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_cancelar_itemActionPerformed
    Tela_novo_item.setVisible(false);
    Menu_itens_selecao.setVisible(true);
    }//GEN-LAST:event_Item_cancelar_itemActionPerformed
    
    private void Item_salvar_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_salvar_itemActionPerformed
     // Recuperando as informaçõe
     
    String nomeItem = Item_nome_item.getText();
    String dataItem = Item_data_item.getText();
    String valorTotalItem = Item_valortotal_item.getText();
    String tipoItem = (String) Item_tipo_item.getSelectedItem();
    String marcaItem = (String) Item_marca_item.getSelectedItem();
    String cadastrouserItem = (String) Item_cadastrouser_item.getSelectedItem();
    int quantidadeItem = (Integer) Item_quantidade_item.getValue();
    String descricaoItem = Item_descricao_item.getText();

   
    Double valorUnidadeItem = null;
    if (Item_valor_unidade_item.getValue() != null) {
        valorUnidadeItem = ((Number) Item_valor_unidade_item.getValue()).doubleValue();
    }

 
    if (valorUnidadeItem == null) {
        JOptionPane.showMessageDialog(null, "O valor unitário não pode ser nulo!");
        return;
    }

    int idItem = getProximoId();

    String itemInfo = String.format(
    "ID: %d\nNome: %s\nData: %s\nValor Total: %s\nTipo: %s\nMarca: %s\nCadastrouser: %s\nQuantidade: %d\nValor Unitário: %.2f\nDescrição: %s\n\n",
    idItem, nomeItem, dataItem, valorTotalItem, tipoItem, marcaItem, cadastrouserItem, quantidadeItem, valorUnidadeItem, descricaoItem
);

try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_ITENS, true))) {
    writer.write(itemInfo);
    writer.flush();
    JOptionPane.showMessageDialog(null, "Item salvo com sucesso! ID: " + idItem);
} catch (IOException e) {
    JOptionPane.showMessageDialog(null, "Erro ao salvar o item: " + e.getMessage());
}

    // Atualiza o ID para o próximo item
    salvarIdAtual(idItem);
    carregarItensNaTabela();
    }//GEN-LAST:event_Item_salvar_itemActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
       Tela_inventario.setVisible(false);
       Menu_itens_selecao.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void organiza_marcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_organiza_marcaActionPerformed
   // Recupera o modelo original da tabela
    DefaultTableModel model = (DefaultTableModel) Inventario_tabela.getModel();
    
    // Cria um TableRowSorter com o modelo da tabela
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    
    // Define o sorter na tabela
    Inventario_tabela.setRowSorter(sorter);
    
    // Define a coluna "Marca" (supondo que seja a 5ª coluna, índice 5)
    sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(5, SortOrder.ASCENDING)));
    
    // Atualiza a ordenação
    sorter.sort();
    }//GEN-LAST:event_organiza_marcaActionPerformed

    private void organiza_dataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_organiza_dataActionPerformed
     // Recupera o modelo original da tabela
    DefaultTableModel model = (DefaultTableModel) Inventario_tabela.getModel();
    
    // Cria um TableRowSorter com o modelo da tabela
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    
    // Define o sorter na tabela
    Inventario_tabela.setRowSorter(sorter);
    
    // Define a coluna "Data" (supondo que seja a 4ª coluna, índice 3) 
    // A data precisa ser comparada corretamente, então usamos um Comparator para isso
    sorter.setComparator(3, (o1, o2) -> {
        try {
            // Converte as strings de data para objetos Date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date d1 = sdf.parse((String) o1);
            Date d2 = sdf.parse((String) o2);
            
            // Compara as datas
            return d1.compareTo(d2);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    });

    // Define a ordenação crescente
    sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(3, SortOrder.ASCENDING)));
    
    // Atualiza a ordenação
    sorter.sort();
    }//GEN-LAST:event_organiza_dataActionPerformed

    private void organiza_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_organiza_itemActionPerformed
// Recupera o modelo original da tabela
    DefaultTableModel model = (DefaultTableModel) Inventario_tabela.getModel();
    
    // Cria um TableRowSorter com o modelo da tabela
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    
    // Define o sorter na tabela
    Inventario_tabela.setRowSorter(sorter);
    
    // Define a coluna "Nome" (supondo que seja a 2ª coluna, índice 2)
    sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(2, SortOrder.ASCENDING)));
    
    // Atualiza a ordenação
    sorter.sort();
    }//GEN-LAST:event_organiza_itemActionPerformed

    private void organiza_tipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_organiza_tipoActionPerformed
// Recupera o modelo original da tabela
    DefaultTableModel model = (DefaultTableModel) Inventario_tabela.getModel();
    
    // Cria um TableRowSorter com o modelo da tabela
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    
    // Define o sorter na tabela
    Inventario_tabela.setRowSorter(sorter);
    
    // Define a coluna "Tipo" (supondo que seja a 4ª coluna, índice 4)
    sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(4, SortOrder.ASCENDING)));
    
    // Atualiza a orden 
    }//GEN-LAST:event_organiza_tipoActionPerformed

    private void Table_pesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Table_pesquisarActionPerformed
                                            
    // Obtém o texto digitado no campo de pesquisa
    String textoPesquisado = Table_pesquisar.getText().trim().toLowerCase();
    
    // Recupera o modelo original da tabela
    DefaultTableModel model = (DefaultTableModel) Inventario_tabela.getModel();
    
    // Cria uma lista para armazenar as linhas originais
    List<Object[]> linhasOriginais = new ArrayList<>();
    
    // Copia todas as linhas do modelo original para a lista
    for (int i = 0; i < model.getRowCount(); i++) {
        Object[] linha = new Object[model.getColumnCount()];
        for (int j = 0; j < model.getColumnCount(); j++) {
            linha[j] = model.getValueAt(i, j);
        }
        linhasOriginais.add(linha);
    }
    
    // Cria uma lista para armazenar as linhas correspondentes à pesquisa
    List<Object[]> linhasFiltradas = new ArrayList<>();
    
    // Filtra as linhas que correspondem ao texto pesquisado
    for (Object[] linha : linhasOriginais) {
        boolean encontrado = false;
        for (Object valor : linha) {
            if (valor != null && valor.toString().toLowerCase().contains(textoPesquisado)) {
                encontrado = true;
                break;
            }
        }
        if (encontrado) {
            linhasFiltradas.add(linha);
        }
    }
    
    // Limpa o modelo atual da tabela
    model.setRowCount(0);
    
    // Adiciona as linhas filtradas de volta ao modelo
    for (Object[] linha : linhasFiltradas) {
        model.addRow(linha);
    }
    
    if (textoPesquisado.isEmpty()) {
        carregarItensNaTabela(); // Restaura os dados originais na tabela
        return;
    }
    
    // Atualiza a tabela
    Inventario_tabela.setModel(model);
    }//GEN-LAST:event_Table_pesquisarActionPerformed

    private void Item_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_deleteActionPerformed
 // Obter o modelo da tabela
    DefaultTableModel model = (DefaultTableModel) Inventario_tabela.getModel();
    int rowCount = model.getRowCount();
    List<Integer> idsParaExcluir = new ArrayList<>();

    // Verificar as linhas com checkbox selecionada
    for (int i = rowCount - 1; i >= 0; i--) { // Iterar de trás para frente para evitar problemas de índice
        boolean isChecked = (boolean) model.getValueAt(i, 0); // Coluna 0 é a checkbox
        if (isChecked) {
            int id = (int) model.getValueAt(i, 1); // Coluna 1 contém o ID do item
            idsParaExcluir.add(id); // Adicionar o ID à lista de exclusão
        }
    }

    // Se nenhuma checkbox foi selecionada, não prosseguir
    if (idsParaExcluir.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nenhum item foi selecionado para exclusão.", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Exibir caixa de diálogo de confirmação
    int confirmacao = JOptionPane.showConfirmDialog(this, 
            "Tem certeza de que deseja excluir os itens selecionados?", 
            "Confirmação", JOptionPane.YES_NO_OPTION);

    // Se o usuário confirmar, excluir os itens
    if (confirmacao == JOptionPane.YES_OPTION) {
        // Remover os itens selecionados do modelo da tabela
        for (int i = rowCount - 1; i >= 0; i--) {
            boolean isChecked = (boolean) model.getValueAt(i, 0); // Coluna 0 é a checkbox
            if (isChecked) {
                model.removeRow(i); // Remove a linha da tabela
            }
        }

        // Atualizar o arquivo excluindo os itens selecionados
        atualizarArquivoItens(idsParaExcluir);
        JOptionPane.showMessageDialog(this, "Itens excluídos com sucesso.", 
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
    }//GEN-LAST:event_Item_deleteActionPerformed

    private void Button_cancelar_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_cancelar_clienteActionPerformed
    Tela_novo_cliente.setVisible(false);
    Menu_clientes_selecao.setVisible(true);
    }//GEN-LAST:event_Button_cancelar_clienteActionPerformed

    private void Button_salvar_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_salvar_clienteActionPerformed
 // Caminho do arquivo para guardar o último ID usado
    String arquivoUltimoID = "ultimoID.txt";
    int proximoID;

    // Tentar ler o último ID do arquivo
    try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(arquivoUltimoID))) {
        proximoID = Integer.parseInt(reader.readLine());
    } catch (java.io.IOException | NumberFormatException e) {
        proximoID = 1;  // Começar do ID 1 se não encontrar
    }

    // Coletando os dados dos campos
    String nome = Cliente_nome.getText().trim();
    String contato = Cliente_contato.getText().trim();
    String endereco = Cliente_endereco.getText().trim();
    String numeroCasa = Cliente_ncasa.getText().trim();
    String bairro = Cliente_bairro.getText().trim();
    String pontoRef = Cliente_pontoref.getText().trim();
    String observacoes = Cliente_obs.getText().trim();

    // Criar uma instância de Telalogin_1 para obter o último usuário logado
    Telalogin_1 telaLogin = new Telalogin_1();
    String nomeUltimoUsuario = telaLogin.obterUltimoUsuarioLogado();  // Nome do último usuário logado

    // Montar os dados a serem salvos
    String dadosCliente = String.format(
        "ID: %d\nNome: %s\nContato: %s\nEndereço: %s\nNúmero: %s\nBairro: %s\nPonto de Referência: %s\nObservações: %s\n\n",
        proximoID, nome, contato, endereco, numeroCasa, bairro, pontoRef, observacoes
    );

    // Salvar no arquivo de clientes
    try (FileWriter writer = new FileWriter("clientes.txt", true)) {
        writer.write(dadosCliente);
        writer.flush();

        // Exibir mensagem de sucesso
        JOptionPane.showMessageDialog(null, "Cliente salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException e) {
        e.printStackTrace();
        // Exibir mensagem de erro
        JOptionPane.showMessageDialog(null, "Erro ao salvar o cliente. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // Atualizar o arquivo de último ID usado para o próximo cadastro
    proximoID++;  // Incrementa o ID
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoUltimoID))) {
        writer.write(String.valueOf(proximoID));  // Salva o próximo ID
    } catch (IOException e) {
        e.printStackTrace();
    }

    PreencherTabela preencherTabela = new PreencherTabela(tabelaClientes); // Passa a JTable
    preencherTabela.carregarDadosDaTabela();  // Carrega os dados da tabela
    }//GEN-LAST:event_Button_salvar_clienteActionPerformed

    private void Cliente_bairroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cliente_bairroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Cliente_bairroActionPerformed

    private void Cliente_ncasaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cliente_ncasaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Cliente_ncasaActionPerformed

    private void Cliente_nomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cliente_nomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Cliente_nomeActionPerformed

    private void organiza_nomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_organiza_nomeActionPerformed
  // Recupera o modelo original da tabela
    DefaultTableModel model = (DefaultTableModel) tabelaClientes.getModel();

    // Cria um TableRowSorter com o modelo da tabela
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

    // Define o sorter na tabela
    tabelaClientes.setRowSorter(sorter);

    // Define o índice da coluna "Nome" (neste caso, índice 2)
    int indiceNome = 2;

    // Define a coluna "Nome" para ordenação (em ordem crescente)
    sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(indiceNome, SortOrder.ASCENDING)));

    // Atualiza a ordenação
    sorter.sort();
    }//GEN-LAST:event_organiza_nomeActionPerformed

    private void organiza_bairroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_organiza_bairroActionPerformed
    // Recupera o modelo da tabela
    DefaultTableModel model = (DefaultTableModel) tabelaClientes.getModel();
    
    // Cria um TableRowSorter com o modelo da tabela
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    
    // Define o sorter na tabela
    tabelaClientes.setRowSorter(sorter);
    
    // Define a coluna "Bairro" (índice 6, pois a contagem começa em 0)
    sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(6, SortOrder.ASCENDING)));
    
    // Atualiza a ordenação
    sorter.sort();
    }//GEN-LAST:event_organiza_bairroActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
    Tela_clientes.setVisible(false);
    Menu_clientes_selecao.setVisible(true);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void Pesquisar_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Pesquisar_clienteActionPerformed
    String filtro = Pesquisar_cliente.getText(); // Obtém o texto do campo
    aplicarFiltroNaTabela(filtro);         // Aplica o filtro
    }//GEN-LAST:event_Pesquisar_clienteActionPerformed

    private void excluir_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excluir_clienteActionPerformed
   // Exibir caixa de confirmação
    int confirmacao = JOptionPane.showConfirmDialog(
        null, 
        "Tem certeza que deseja excluir os clientes selecionados?", 
        "Confirmação", 
        JOptionPane.YES_NO_OPTION
    );
    

    // Verifica se o usuário clicou em "Sim"
    if (confirmacao == JOptionPane.YES_OPTION) {
        DefaultTableModel model = (DefaultTableModel) tabelaClientes.getModel();
        int totalLinhas = model.getRowCount();
        List<String> idsParaExcluir = new ArrayList<>();

        // Percorrer a tabela para encontrar os itens selecionados
        for (int i = totalLinhas - 1; i >= 0; i--) {
            Boolean selecionado = (Boolean) model.getValueAt(i, 0); // Coluna 0 é "Selecionar"
            if (Boolean.TRUE.equals(selecionado)) {
                String id = model.getValueAt(i, 1).toString(); // Coluna 1 é "ID"
                idsParaExcluir.add(id); // Adiciona o ID à lista
                model.removeRow(i); // Remove a linha da tabela
            }
        }

        // Atualizar o arquivo "clientes.txt"
        if (!idsParaExcluir.isEmpty()) {
            try {
                atualizarArquivoClientes(idsParaExcluir);
                JOptionPane.showMessageDialog(null, "Clientes excluídos com sucesso!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao atualizar o arquivo de clientes.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum cliente foi selecionado para exclusão.");
        }
    }
    }//GEN-LAST:event_excluir_clienteActionPerformed

    private void venda_enderecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_venda_enderecoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_venda_enderecoActionPerformed

    private void venda_numcasaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_venda_numcasaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_venda_numcasaActionPerformed

    private void Combobox_pagamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Combobox_pagamentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Combobox_pagamentoActionPerformed

    private void venda_valorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_venda_valorActionPerformed
    double valorTotal = 0.0; // Variável para armazenar o valor total

// Obtém o modelo da tabela
DefaultTableModel model = (DefaultTableModel) venda_tabela_listacompra.getModel();

// Itera por cada linha da tabela
for (int i = 0; i < model.getRowCount(); i++) {
    try {
        // Obtém o valor da quantidade (coluna 2) e do valor unitário (coluna 3)
        String quantidadeStr = model.getValueAt(i, 2).toString(); // Pega como String
        int quantidade = Integer.parseInt(quantidadeStr); // Converte para Integer

        double valorUnitario = (double) model.getValueAt(i, 3); // Coluna 3 é o valor unitário

        // Calcula o valor total da linha (quantidade * valor unitário)
        valorTotal += quantidade * valorUnitario;
    } catch (NumberFormatException e) {
        // Se não conseguir converter para Integer, imprime o erro
        System.out.println("Erro ao converter quantidade para número: " + e.getMessage());
    }
}

// Atualiza o campo de valor total (JTextField)
venda_valor.setText(String.format("R$ %.2f", valorTotal));

// Atualiza o valor total com o desconto
atualizarValorTotal();
    }//GEN-LAST:event_venda_valorActionPerformed

    private void venda_descontoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_venda_descontoActionPerformed
    try {
        // Recupera o valor do desconto inserido
        String descontoTexto = venda_desconto.getText();
        descontoTexto = descontoTexto.replace(",", ".").replaceAll("[^0-9.]", ""); // Limpa caracteres inválidos

        // Converte o texto para double
        valorDesconto = Double.parseDouble(descontoTexto);

        // Atualiza o valor total
        atualizarValorTotal();
    } catch (NumberFormatException e) {
        System.out.println("Erro ao processar o desconto: " + e.getMessage());
    }
    }//GEN-LAST:event_venda_descontoActionPerformed

    private void venda_valor_totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_venda_valor_totalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_venda_valor_totalActionPerformed

    private void button_adicionar_listacompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_adicionar_listacompraActionPerformed
    // Obtém o modelo de ambas as tabelas
    DefaultTableModel modelItens = (DefaultTableModel) venda_tabela_itens.getModel();
    DefaultTableModel modelListaCompra = (DefaultTableModel) venda_tabela_listacompra.getModel();

    // Percorre todas as linhas da tabela de itens
    for (int i = 0; i < modelItens.getRowCount(); i++) {
        // Verifica se o checkbox está marcado (primeira coluna)
        boolean isSelected = (boolean) modelItens.getValueAt(i, 0);
        
        if (isSelected) {
            // Obtém os dados do item selecionado
            int id = (int) modelItens.getValueAt(i, 1);
            String nome = (String) modelItens.getValueAt(i, 2);
            int quantidade = 0; // Inicializa a quantidade como 0 (vai ser preenchida depois)
            double valorUnitario = (double) modelItens.getValueAt(i, 4);
            
            // Adiciona o item à tabela de lista de compras
            modelListaCompra.addRow(new Object[] {
                id,
                nome,
                quantidade, // Quantidade será preenchida pelo usuário depois
                valorUnitario
            });
        }
    }

    // Aplica o modelo atualizado à tabela de lista de compras
    venda_tabela_listacompra.setModel(modelListaCompra);
    calcularValorTotal();
    }//GEN-LAST:event_button_adicionar_listacompraActionPerformed

    private void venda_dataentregaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_venda_dataentregaActionPerformed
    String texto = ((javax.swing.JTextField) evt.getSource()).getText();

    // Remover todos os caracteres não numéricos
    texto = texto.replaceAll("[^0-9]", "");

    // Limitar o tamanho máximo para 8 caracteres (DDMMAAAA)
    if (texto.length() > 8) {
        texto = texto.substring(0, 8);
    }

    // Formatar como DD/MM/AAAA
    StringBuilder dataFormatada = new StringBuilder();

    if (texto.length() >= 2) {
        dataFormatada.append(texto.substring(0, 2)).append("/"); // Dia
    }
    if (texto.length() >= 4) {
        dataFormatada.append(texto.substring(2, 4)).append("/"); // Mês
    }
    if (texto.length() > 4) {
        dataFormatada.append(texto.substring(4)); // Ano
    }

    // Atualizar o campo com o texto formatado
    ((javax.swing.JTextField) evt.getSource()).setText(dataFormatada.toString());
    }//GEN-LAST:event_venda_dataentregaActionPerformed

    private void remover_itensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remover_itensActionPerformed
     // Obtém o modelo da tabela
    DefaultTableModel model = (DefaultTableModel) venda_tabela_listacompra.getModel();

    // Limpa todos os dados da tabela
    model.setRowCount(0);

    // Atualiza o valor total para 0
    venda_valor.setText("R$ 0.00");

    // Atualiza o valor total com desconto para 0
    venda_valor_total.setText("R$ 0.00");
    }//GEN-LAST:event_remover_itensActionPerformed

    private void Item_valortotal_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_valortotal_itemActionPerformed
    atualizarValorTotalteste();        // TODO add your handling code here:
    }//GEN-LAST:event_Item_valortotal_itemActionPerformed

    private void Item_cadastrouser_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_cadastrouser_itemActionPerformed

    }//GEN-LAST:event_Item_cadastrouser_itemActionPerformed

    private void organiza_enderecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_organiza_enderecoActionPerformed
    // Recupera o modelo da tabela
    DefaultTableModel model = (DefaultTableModel) tabelaClientes.getModel();
    
    // Cria um TableRowSorter com o modelo da tabela
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    
    // Define o sorter na tabela
    tabelaClientes.setRowSorter(sorter);
    
    // Define a coluna "Endereço" (índice 4, pois a contagem começa em 0)
    sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(4, SortOrder.ASCENDING)));
    
    // Atualiza a ordenação
    sorter.sort();
    }//GEN-LAST:event_organiza_enderecoActionPerformed

    private void Pesquisar_vendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Pesquisar_vendaActionPerformed
// Pega o texto digitado no campo de pesquisa
    String textoPesquisa = Pesquisar_venda.getText().trim(); // Pega o texto digitado no campo de pesquisa

    // Verifica se o texto não está vazio para aplicar o filtro
    TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) tabela_vendas.getRowSorter();
    if (!textoPesquisa.isEmpty()) {
        // Aplica o filtro de pesquisa (case-insensitive)
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + textoPesquisa));
    } else {
        // Se o campo estiver vazio, mostra todas as linhas
        sorter.setRowFilter(null);
    }
    }//GEN-LAST:event_Pesquisar_vendaActionPerformed

    private void venda_bairroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_venda_bairroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_venda_bairroActionPerformed

    private void venda_contatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_venda_contatoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_venda_contatoActionPerformed

    private void Button_editar_usuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_editar_usuariosActionPerformed
// Criação da janela de diálogo
    JDialog janelaEditarUsuarios = new JDialog(this, "Editar Usuários", true);
    janelaEditarUsuarios.setSize(600, 400);
    janelaEditarUsuarios.setLocationRelativeTo(this);

    // Definir os títulos das colunas
    String[] colunas = {"Selecionar", "Usuário", "Senha"};

    // Criar o modelo da tabela
    DefaultTableModel model = new DefaultTableModel(null, colunas) {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class; // Primeira coluna será checkbox
            }
            return String.class;
        }
    };

    // Criar a tabela
    JTable tabelaUsuarios = new JTable(model);
    tabelaUsuarios.setFillsViewportHeight(true);

    // Configurar a coluna de checkbox
    TableColumn checkboxColumn = tabelaUsuarios.getColumnModel().getColumn(0);
    checkboxColumn.setPreferredWidth(50);
    checkboxColumn.setMaxWidth(50);
    checkboxColumn.setMinWidth(50);

    // Adicionar tabela a um painel de rolagem
    JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);
    janelaEditarUsuarios.add(scrollPane, BorderLayout.CENTER);

    // Painel para os botões
    JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));

    // Botão para salvar alterações
    JButton botaoSalvar = new JButton("Salvar Seleções");
    botaoSalvar.addActionListener(e -> {
    // Lista para armazenar os dados atualizados
    List<String> dadosAtualizados = new ArrayList<>();

    // Iterar por todas as linhas da tabela
    for (int i = 0; i < tabelaUsuarios.getRowCount(); i++) {
        String usuario = (String) tabelaUsuarios.getValueAt(i, 1); // Nome do usuário
        String senha = (String) tabelaUsuarios.getValueAt(i, 2);   // Senha do usuário

        // Adicionar ao arquivo no formato correto
        dadosAtualizados.add("Nome: " + usuario);
        dadosAtualizados.add("Senha: " + senha);
    }

    // Reescrever o arquivo "cadastros.txt" com os dados atualizados
    String arquivoUsuarios = "cadastros.txt";
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoUsuarios))) {
        for (String linha : dadosAtualizados) {
            writer.write(linha);
            writer.newLine();
        }
        JOptionPane.showMessageDialog(janelaEditarUsuarios, "Alterações salvas com sucesso!", "Concluído", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(janelaEditarUsuarios, "Erro ao salvar as alterações no arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
});

    // Botão para excluir selecionados
    JButton botaoExcluir = new JButton("Excluir Selecionados");
    botaoExcluir.addActionListener(e -> {
        // Lista para armazenar os usuários que serão mantidos
        List<String> usuariosMantidos = new ArrayList<>();

        for (int i = 0; i < tabelaUsuarios.getRowCount(); i++) {
            boolean selecionado = (boolean) tabelaUsuarios.getValueAt(i, 0);
            String usuario = (String) tabelaUsuarios.getValueAt(i, 1);
            String senha = (String) tabelaUsuarios.getValueAt(i, 2);

            if (!selecionado) {
                // Adicionar usuários não selecionados à lista
                usuariosMantidos.add("Nome: " + usuario);
                usuariosMantidos.add("Senha: " + senha);
            }
        }

        // Atualizar o arquivo cadastros.txt com os usuários mantidos
        String arquivoUsuarios = "cadastros.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoUsuarios))) {
            for (String linha : usuariosMantidos) {
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(janelaEditarUsuarios, "Erro ao salvar as alterações no arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Remover as linhas selecionadas da tabela
        for (int i = tabelaUsuarios.getRowCount() - 1; i >= 0; i--) {
            boolean selecionado = (boolean) tabelaUsuarios.getValueAt(i, 0);
            if (selecionado) {
                model.removeRow(i); // Remove a linha da tabela
            }
        }

        JOptionPane.showMessageDialog(janelaEditarUsuarios, "Usuários selecionados foram excluídos com sucesso!", "Concluído", JOptionPane.INFORMATION_MESSAGE);
    });

    // Adicionar os botões ao painel
    painelBotoes.add(botaoSalvar);
    painelBotoes.add(botaoExcluir);

    // Adicionar o painel de botões à parte inferior da janela
    janelaEditarUsuarios.add(painelBotoes, BorderLayout.SOUTH);

    // Ler dados do arquivo "cadastros.txt"
    String arquivoUsuarios = "cadastros.txt";
    try (BufferedReader reader = new BufferedReader(new FileReader(arquivoUsuarios))) {
        String linha;
        while ((linha = reader.readLine()) != null) {
            // Procurar entradas de "Nome" e "Senha"
            if (linha.startsWith("Nome: ")) {
                String nome = linha.split(":")[1].trim(); // Extrai o nome
                String senha = reader.readLine().split(":")[1].trim(); // Lê a próxima linha para a senha
                model.addRow(new Object[]{false, nome, senha}); // Adiciona à tabela
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Erro ao carregar os dados do arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    // Exibir a janela
    janelaEditarUsuarios.setVisible(true);
    }//GEN-LAST:event_Button_editar_usuariosActionPerformed

    private void Usuario_onActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Usuario_onActionPerformed
    String nomeUsuario = Usuario_on.getText();
    }//GEN-LAST:event_Usuario_onActionPerformed

    private void button_excluir_vendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_excluir_vendaActionPerformed
 DefaultTableModel model = (DefaultTableModel) tabela_vendas.getModel();

// Listar as linhas para excluir
List<Integer> linhasParaExcluir = new ArrayList<>();
for (int i = 0; i < model.getRowCount(); i++) {
    // Verificar se a checkbox da linha está selecionada (true)
    boolean isChecked = (boolean) model.getValueAt(i, 0); // Coluna 0 é a coluna da checkbox
    if (isChecked) {
        linhasParaExcluir.add(i);
    }
}

// Excluir as linhas da tabela (começando do fim para evitar problemas de índices)
for (int i = linhasParaExcluir.size() - 1; i >= 0; i--) {
    model.removeRow(linhasParaExcluir.get(i));
}

// Agora, vamos remover as vendas do arquivo Vendas.txt
removerVendasDoArquivo(linhasParaExcluir);
}

private void removerVendasDoArquivo(List<Integer> linhasParaExcluir) {
     // Caminho do arquivo de vendas
    String arquivoVendas = "Vendas.txt";
    
    // Criar uma lista para armazenar as linhas restantes
    List<String> linhasRestantes = new ArrayList<>();
    
    try (BufferedReader reader = new BufferedReader(new FileReader(arquivoVendas))) {
        String linha;
        int linhaAtual = 0;
        boolean excluirVenda = false;

        // Lê o arquivo linha por linha
        while ((linha = reader.readLine()) != null) {
            // Verificar se a linha atual é a de uma "Venda ID" e se a venda precisa ser excluída
            if (linha.startsWith("Venda ID")) {
                excluirVenda = linhasParaExcluir.contains(linhaAtual); // Verifica se deve excluir esta venda
            }

            // Se não for uma venda a ser excluída, adiciona à lista de linhas restantes
            if (!excluirVenda) {
                linhasRestantes.add(linha);
            }
            linhaAtual++;
        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erro ao ler o arquivo de vendas.", "Erro", JOptionPane.ERROR_MESSAGE);
    }
    
    // Agora, sobrescreve o arquivo com as vendas restantes
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoVendas))) {
        for (String linha : linhasRestantes) {
            writer.write(linha);
            writer.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erro ao atualizar o arquivo de vendas.", "Erro", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_button_excluir_vendaActionPerformed

    private void organiza_clientes_vendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_organiza_clientes_vendaActionPerformed
    // Cria um TableRowSorter para ordenar a tabela
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) tabela_vendas.getModel());
    
    // Define a ordenação para a coluna "Cliente" (índice 2) em ordem alfabética
    sorter.setComparator(2, new Comparator<String>() {
        @Override
        public int compare(String cliente1, String cliente2) {
            return cliente1.compareToIgnoreCase(cliente2); // Ignora a diferença entre maiúsculas e minúsculas
        }
    });

    // Aplica o sorter à tabela
    tabela_vendas.setRowSorter(sorter);

    // Ordena a tabela pela coluna Cliente
    sorter.toggleSortOrder(2); 
    }//GEN-LAST:event_organiza_clientes_vendaActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
    Tela_consultar_vendas.setVisible(false);
    Menu_vendas_selecao.setVisible(true);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void organiza_entregue_vendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_organiza_entregue_vendaActionPerformed
    // Obtém o modelo da tabela
    DefaultTableModel model = (DefaultTableModel) tabela_vendas.getModel();

    // Cria um TableRowSorter para aplicar o filtro
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

    // Aplica o filtro para mostrar apenas as linhas com "ENTREGUE" na coluna de estado (coluna 11)
    sorter.setRowFilter(RowFilter.regexFilter("ENTREGUE", 11)); // A coluna 11 é onde o estado "ENTREGUE" está armazenado

    // Aplica o sorter à tabela
    tabela_vendas.setRowSorter(sorter);
    }//GEN-LAST:event_organiza_entregue_vendaActionPerformed

    private void organiza_pendente_vendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_organiza_pendente_vendaActionPerformed
    // Obtém o modelo da tabela
    DefaultTableModel model = (DefaultTableModel) tabela_vendas.getModel();

    // Cria um TableRowSorter para aplicar o filtro
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

    // Aplica o filtro para mostrar apenas as linhas com "ENTREGUE" na coluna de estado (coluna 11)
    sorter.setRowFilter(RowFilter.regexFilter("Pendente", 11)); // A coluna 11 é onde o estado "ENTREGUE" está armazenado

    // Aplica o sorter à tabela
    tabela_vendas.setRowSorter(sorter);
    }//GEN-LAST:event_organiza_pendente_vendaActionPerformed

    private void button_entregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_entregarActionPerformed
    // Exibe um diálogo de confirmação
    int resposta = JOptionPane.showConfirmDialog(
        null, 
        "Você tem certeza que deseja alterar o estado das vendas selecionadas para 'ENTREGUE'?", 
        "Confirmar Alteração", 
        JOptionPane.YES_NO_OPTION, 
        JOptionPane.QUESTION_MESSAGE
    );
    
    // Se o usuário clicar em "Sim" (JOptionPane.YES_OPTION)
    if (resposta == JOptionPane.YES_OPTION) {
        // Obtém o modelo da tabela
        DefaultTableModel model = (DefaultTableModel) tabela_vendas.getModel();

        // Itera sobre as linhas da tabela para verificar se o checkbox está selecionado
        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean isSelected = (Boolean) model.getValueAt(i, 0); // Obtém o valor do checkbox na coluna 0

            if (isSelected) {
                // Atualiza o estado da venda para "ENTREGUE" (coluna "Estado" está na posição 11)
                model.setValueAt("ENTREGUE", i, 11); 
            }
        }

        // Atualizar o arquivo Vendas.txt para refletir a mudança
        atualizarArquivoVendas();
        
        // Informar o usuário que as vendas foram alteradas
        JOptionPane.showMessageDialog(null, "As vendas selecionadas foram alteradas para 'ENTREGUE'.", "Alteração Confirmada", JOptionPane.INFORMATION_MESSAGE);
    } else {
        // Caso o usuário clique em "Não", não faz nada
        JOptionPane.showMessageDialog(null, "Nenhuma venda foi alterada.", "Operação Cancelada", JOptionPane.INFORMATION_MESSAGE);
    }
    }//GEN-LAST:event_button_entregarActionPerformed

    private void Button_limparFiltrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_limparFiltrosActionPerformed
     // Obtém o modelo da tabela
    DefaultTableModel model = (DefaultTableModel) tabela_vendas.getModel();

    // Cria um TableRowSorter para a tabela
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

    // Remove o filtro para mostrar todas as vendas
    sorter.setRowFilter(null);

    // Aplica o sorter à tabela
    tabela_vendas.setRowSorter(sorter);
    }//GEN-LAST:event_Button_limparFiltrosActionPerformed

    private void Button_desconectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_desconectarActionPerformed
    // Exibe um diálogo de confirmação com opções em português
    Object[] options = {"Sim", "Não"};
    int confirmacao = JOptionPane.showOptionDialog(
        null, 
        "Você realmente deseja se desconectar?", 
        "Confirmar Desconexão", 
        JOptionPane.YES_NO_OPTION, // Define a quantidade de opções (Sim/Não)
        JOptionPane.QUESTION_MESSAGE, 
        null, // Não há ícone extra
        options, // Opções personalizadas
        options[0] // Definir "Sim" como opção padrão
    );

    // Se o usuário clicar em "Sim", fecha o Menu_principal e abre o Telalogin_1
    if (confirmacao == 0) {  // 0 é o índice de "Sim" nas opções
        // Fecha a janela do Menu_principal
        this.dispose();  // Fechando o Menu_principal (supondo que esta ação esteja na janela Menu_principal)

        // Abre a janela Telalogin_1
        Telalogin_1 loginTela = new Telalogin_1();
        loginTela.setVisible(true); // Torna a tela de login visível
    }
    }//GEN-LAST:event_Button_desconectarActionPerformed

private void atualizarArquivoClientes(List<String> idsParaExcluir) throws IOException {
    File arquivoClientes = new File("clientes.txt");
    File arquivoTemp = new File("clientes_temp.txt");

    try (BufferedReader reader = new BufferedReader(new FileReader(arquivoClientes));
         BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoTemp))) {
        String linha;
        boolean pularCliente = false;

        while ((linha = reader.readLine()) != null) {
            if (linha.startsWith("ID:")) {
                String id = linha.substring(3).trim();
                pularCliente = idsParaExcluir.contains(id); // Verifica se o cliente deve ser excluído
            }

            // Copiar as linhas para o novo arquivo, exceto os clientes a serem excluídos
            if (!pularCliente) {
                writer.write(linha);
                writer.newLine();
            }

            // Se chegar em uma linha em branco, termina o cliente atual
            if (linha.trim().isEmpty()) {
                pularCliente = false;
            }
        }
    }

    // Substituir o arquivo original pelo temporário
    if (arquivoClientes.delete() && arquivoTemp.renameTo(arquivoClientes)) {
        System.out.println("Arquivo atualizado com sucesso!");
    } else {
        throw new IOException("Erro ao salvar as alterações no arquivo.");
    }
}
   
// Método para aplicar o filtro à tabela
private void aplicarFiltroNaTabela(String filtro) {
    DefaultTableModel modelo = (DefaultTableModel) tabelaClientes.getModel();

    // Verifica se o TableRowSorter já foi adicionado
    if (tabelaClientes.getRowSorter() == null) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        tabelaClientes.setRowSorter(sorter);
    }

    // Obtém o TableRowSorter
    TableRowSorter<?> sorter = (TableRowSorter<?>) tabelaClientes.getRowSorter();

    // Aplica o filtro
    if (filtro.isEmpty()) {
        sorter.setRowFilter(null); // Mostra todos os dados se o campo estiver vazio
    } else {
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + filtro)); // Filtro que ignora maiúsculas/minúsculas
    }
}
    
    ////teste gigatonico///
    ///////////////////////
private void carregarItensNaTabela() {
    // Recupera os dados do arquivo
    List<Item> listaDeItens = carregarItensDoArquivo();

    // Cria o modelo da tabela
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0; // Permite edição apenas na coluna da checkbox (coluna 0)
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class; // Primeira coluna é para JCheckBox
            }
            return String.class; // As outras colunas são String
        }
    };

    // Adiciona as colunas
    model.addColumn(""); // Coluna para as checkboxes
    model.addColumn("ID");
    model.addColumn("Nome");
    model.addColumn("Data");
    model.addColumn("Tipo");
    model.addColumn("Marca");
    model.addColumn("CadastradoPor");
    model.addColumn("Quantidade");
    model.addColumn("Valor Unitário");
    model.addColumn("Descrição");

    // Adiciona os itens ao modelo da tabela
    for (Item item : listaDeItens) {
        model.addRow(new Object[] {
            false,  // Inicia com a checkbox desmarcada
            item.getId(),
            item.getNome(),
            item.getData(),
            item.getTipo(),
            item.getMarca(),
            item.getCadastradoPor(),
            item.getQuantidade(),
            item.getValorUnitario(),
            item.getDescricao()
        });
    }

    // Atualiza o JTable com o modelo
    Inventario_tabela.setModel(model);

    // Ajusta a coluna das checkboxes
    TableColumn checkboxColumn = Inventario_tabela.getColumnModel().getColumn(0);
    checkboxColumn.setPreferredWidth(30); // Define uma largura estreita
    checkboxColumn.setMinWidth(30);
    checkboxColumn.setMaxWidth(30);

    // Ajusta a coluna do ID
    TableColumn idColumn = Inventario_tabela.getColumnModel().getColumn(1);
    idColumn.setPreferredWidth(50); // Define uma largura menor para a coluna ID
    idColumn.setMinWidth(50);
    idColumn.setMaxWidth(50);

    // Redimensionamento automático (apenas as colunas especificadas são afetadas)
    Inventario_tabela.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
}

public List<Item> getItensSelecionados() {
    List<Item> itensSelecionados = new ArrayList<>();
    DefaultTableModel model = (DefaultTableModel) Inventario_tabela.getModel();
    
    // Percorre as linhas da tabela
    for (int i = 0; i < model.getRowCount(); i++) {
        Boolean selecionado = (Boolean) model.getValueAt(i, 0); // Pega o valor da checkbox
        if (selecionado != null && selecionado) {
            // Adiciona o item à lista se a checkbox estiver selecionada
            int id = (int) model.getValueAt(i, 1);
            String nome = (String) model.getValueAt(i, 2);
            String data = (String) model.getValueAt(i, 3);
            String tipo = (String) model.getValueAt(i, 4);
            String marca = (String) model.getValueAt(i, 5);
            String cadastradoPor = (String) model.getValueAt(i, 6);
            int quantidade = (int) model.getValueAt(i, 7);
            double valorUnitario = (double) model.getValueAt(i, 8);
            String descricao = (String) model.getValueAt(i, 9);

            Item item = new Item(id, nome, data, tipo, marca, cadastradoPor, quantidade, valorUnitario, descricao);
            itensSelecionados.add(item);
        }
    }
    return itensSelecionados;
}

public List<Item> carregarItensDoArquivo() {
    List<Item> listaDeItens = new ArrayList<>();
    
    try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_ITENS))) {
        String linha;
        while ((linha = br.readLine()) != null) {
            if (linha.startsWith("ID: ")) {
                // Parse dos dados
                int id = Integer.parseInt(linha.split(":")[1].trim());
                String nome = br.readLine().split(":")[1].trim();
                String data = br.readLine().split(":")[1].trim();
                // Pega o valorTotal mas não usa
                String valorTotalStr = br.readLine().split(":")[1].trim();
                String tipo = br.readLine().split(":")[1].trim();
                String marca = br.readLine().split(":")[1].trim();
                String cadastradoPor = br.readLine().split(":")[1].trim();
                
                // Tratar quantidade para verificar se é um número ou "AVULSO"
                String quantidadeStr = br.readLine().split(":")[1].trim();
                int quantidade = 0;
                if (!quantidadeStr.equals("AVULSO")) {
                    try {
                        quantidade = Integer.parseInt(quantidadeStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Quantidade inválida: " + quantidadeStr);
                    }
                }
                
                // Tratar valor unitário substituindo vírgula por ponto
                double valorUnitario = 0;
                try {
                    String valorUnitarioStr = br.readLine().split(":")[1].trim();
                    valorUnitarioStr = valorUnitarioStr.replace(",", ".");  // Substitui vírgula por ponto
                    valorUnitario = Double.parseDouble(valorUnitarioStr);
                } catch (NumberFormatException e) {
                    System.out.println("Valor unitário inválido");
                }

                String descricao = br.readLine().split(":")[1].trim();

                // Cria o item usando os dados
                Item item = new Item(id, nome, data, tipo, marca, cadastradoPor, quantidade, valorUnitario, descricao);
                listaDeItens.add(item);

                br.readLine(); // Pula a linha em branco entre os itens
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return listaDeItens;
}

public class Item {
    private int id;
    private String nome;
    private String data;
    private String tipo;
    private String marca;
    private String CadastradoPor;
    private int quantidade;
    private double valorUnitario;
    private String descricao;

    // Construtor
    public Item(int id, String nome, String data, String tipo, 
                String marca, String CadastradoPor, int quantidade, double valorUnitario, String descricao) {
        this.id = id;
        this.nome = nome;
        this.data = data;
        this.tipo = tipo;
        this.marca = marca;
        this.CadastradoPor = CadastradoPor;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.descricao = descricao;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getData() {
        return data;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMarca() {
        return marca;
    }

    public String getCadastradoPor() {
        return CadastradoPor;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public String getDescricao() {
        return descricao;
    }

    // Você pode adicionar outros métodos conforme necessário
}

public class ItemTableModel extends AbstractTableModel {
    private List<Item> itens; // Lista de itens

    private final String[] colunas = {
        "ID", "Nome", "Data", "Tipo", "Marca", "Cadastrado Por", "Quantidade", "Valor Unitário", "Descrição"
    };

    public ItemTableModel(List<Item> itens) {
        this.itens = itens;
    }

    @Override
    public int getRowCount() {
        return itens.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item item = itens.get(rowIndex);
        switch (columnIndex) {
            case 0: return item.getId();
            case 1: return item.getNome();
            case 2: return item.getData();
            case 4: return item.getTipo();
            case 5: return item.getMarca();
            case 6: return item.getCadastradoPor();
            case 7: return item.getQuantidade();
            case 8: return item.getValorUnitario();
            case 9: return item.getDescricao();
            default: return null;
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        return colunas[columnIndex];
    }

    public void setItens(List<Item> itens) {
        this.itens = itens;
        fireTableDataChanged();  // Atualiza a tabela
    }
}

private void atualizarArquivoItens(List<Integer> idsParaExcluir) {
    File arquivo = new File(ARQUIVO_ITENS);
    List<String> linhasAtualizadas = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
        String linha;
        boolean excluir = false;

        while ((linha = br.readLine()) != null) {
            if (linha.startsWith("ID: ")) {
                int id = Integer.parseInt(linha.split(":")[1].trim());
                excluir = idsParaExcluir.contains(id); // Verifica se este ID deve ser excluído
            }

            if (!excluir) {
                linhasAtualizadas.add(linha); // Adiciona a linha ao arquivo atualizado
            }

            if (linha.isBlank()) {
                excluir = false; // Reinicia o estado para o próximo item
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Reescrever o arquivo sem os itens excluídos
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, false))) {
        for (String linha : linhasAtualizadas) {
            bw.write(linha);
            bw.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

///carregar usuarios na tela de cadastro de item///
private void carregarNomesNoComboBox() {
    // Caminho do arquivo
    String caminhoArquivo = "cadastros.txt";

    // Limpa todos os itens existentes no JComboBox
    Item_cadastrouser_item.removeAllItems();

    try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
        String linha;
        while ((linha = br.readLine()) != null) {
            if (linha.startsWith("Nome:")) {
                // Extrai o nome do arquivo e adiciona ao JComboBox
                String nome = linha.split(":")[1].trim();
                Item_cadastrouser_item.addItem(nome);
            }
        }

        // Verifica se há itens e seleciona o primeiro (opcional)
        if (Item_cadastrouser_item.getItemCount() > 0) {
            Item_cadastrouser_item.setSelectedIndex(0);
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Erro ao carregar nomes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // Atualiza a interface
    Item_cadastrouser_item.revalidate();
    Item_cadastrouser_item.repaint();
}

///CLIENTES///
//////////////

public class PreencherTabela {
    private JTable tabelaClientes;

    public PreencherTabela(JTable tabelaClientes) {
        this.tabelaClientes = tabelaClientes;
    }

    public void carregarDadosDaTabela() {
        // Cria o modelo da tabela
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Permite edição apenas na coluna de checkbox
                return column == 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) { // A primeira coluna é de checkboxes
                    return Boolean.class;
                }
                return String.class; // Outras colunas são texto
            }
        };

        // Adiciona as colunas
        model.addColumn(""); // Coluna para checkboxes
        model.addColumn("ID");
        model.addColumn("Nome");
        model.addColumn("Contato");
        model.addColumn("Endereço");
        model.addColumn("Número");
        model.addColumn("Bairro");

        // Ler o arquivo de clientes
        try (BufferedReader reader = new BufferedReader(new FileReader("clientes.txt"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.startsWith("ID:")) {
                    // Extrair os dados
                    String id = extractValue(linha, "ID:");
                    String nome = extractValue(reader.readLine(), "Nome:");
                    String contato = extractValue(reader.readLine(), "Contato:");
                    String endereco = extractValue(reader.readLine(), "Endereço:");
                    String numero = extractValue(reader.readLine(), "Número:");
                    String bairro = extractValue(reader.readLine(), "Bairro:");

                    // Debug: Imprime os dados para verificação
                    System.out.println("ID: " + id + ", Nome: " + nome + ", Contato: " + contato + ", Bairro: " + bairro);

                    // Adiciona os dados na tabela, com checkbox inicialmente desmarcado
                    model.addRow(new Object[] {false, id, nome, contato, endereco, numero, bairro});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Atualiza o modelo da tabela
        tabelaClientes.setModel(model);
        TableColumn colunaSelecionar = tabelaClientes.getColumnModel().getColumn(0);
        colunaSelecionar.setPreferredWidth(30); // Define uma largura adequada
        colunaSelecionar.setMaxWidth(30);      // Limita a largura máxima
        colunaSelecionar.setMinWidth(30);      // Define a largura mínima
        
        TableColumn colunaID = tabelaClientes.getColumnModel().getColumn(1);  // Coluna 1 é a coluna "ID"
        colunaID.setPreferredWidth(50);   // Define a largura da coluna ID
        colunaID.setMaxWidth(50);        // Limita a largura máxima da coluna ID
        colunaID.setMinWidth(50);        // Define a largura mínima da coluna ID
    }

    // Método auxiliar para extrair o valor após um prefixo
    private String extractValue(String linha, String prefixo) {
        if (linha != null && linha.startsWith(prefixo)) {
            return linha.substring(prefixo.length()).trim();
        }
        return "";
    }
}


///VENDAS///
////////////

public void preencherTabelaVendas() {
    JTable tabelaVendas = tabela_vendas;

    // Definir os títulos das colunas, incluindo a coluna de "Estado"
    String[] colunas = {"", "ID", "Cliente", "Endereço", "Número", "Bairro", "Contato", "Data de Entrega", "Pagamento", "Valor Total", "Produtos Vendidos", "Estado"};

    // Inicializar o modelo da tabela
    DefaultTableModel model = new DefaultTableModel(null, colunas) {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class; // Checkbox
            }
            return super.getColumnClass(columnIndex);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0; // Apenas a checkbox é editável
        }
    };

    // Criar e configurar o TableRowSorter
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    tabelaVendas.setRowSorter(sorter); // Aplicar o sorter na tabela

    // Criar o modelo e configurar a tabela
    tabelaVendas.setModel(model);
    model.setRowCount(0); // Limpar a tabela antes de preencher

    // Caminho do arquivo
    String arquivoVendas = "Vendas.txt";

    try (BufferedReader reader = new BufferedReader(new FileReader(arquivoVendas))) {
        String linha;
        List<String> produtos = new ArrayList<>();
        while ((linha = reader.readLine()) != null) {
            linha = linha.trim(); // Remover espaços em branco extras
            if (linha.isEmpty()) {
                continue; // Ignorar linhas em branco
            }

            if (linha.startsWith("Venda ID")) {
                if (!produtos.isEmpty()) {
                    // Adicionar produtos ao registro anterior, se necessário
                    String produtosStr = String.join("; ", produtos);
                    model.setValueAt(produtosStr, model.getRowCount() - 1, 10); // Coluna Produtos Vendidos
                    produtos.clear();
                }

                // Processar uma nova venda
                String vendaId = obterValorDaLinha(linha, "Venda ID");
                String cliente = obterValorDaLinha(reader.readLine(), "Cliente");
                String endereco = obterValorDaLinha(reader.readLine(), "Endereço");
                String numero = obterValorDaLinha(reader.readLine(), "Número");
                String bairro = obterValorDaLinha(reader.readLine(), "Bairro");
                String contato = obterValorDaLinha(reader.readLine(), "Contato");
                String dataEntrega = obterValorDaLinha(reader.readLine(), "Data de Entrega");
                String pagamento = obterValorDaLinha(reader.readLine(), "Pagamento");
                String valorTotal = obterValorDaLinha(reader.readLine(), "Valor Total");

                // Aqui verificamos se há o campo "Estado"
                String estado = null;
                linha = reader.readLine(); // Tenta ler a linha do estado
                if (linha != null && linha.startsWith("Estado")) {
                    estado = obterValorDaLinha(linha, "Estado"); // Se o campo "Estado" estiver presente
                }

                // Adicionar os dados da venda à tabela (produtos serão adicionados depois)
                model.addRow(new Object[]{false, vendaId, cliente, endereco, numero, bairro, contato, dataEntrega, pagamento, valorTotal, "", estado});
            } else if (linha.startsWith("Produto:")) {
                produtos.add(linha); // Acumular detalhes do produto
            }
        }

        // Adicionar os produtos da última venda, se houver
        if (!produtos.isEmpty()) {
            String produtosStr = String.join("; ", produtos);
            model.setValueAt(produtosStr, model.getRowCount() - 1, 10); // Coluna Produtos Vendidos
        }

    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erro ao carregar os dados da venda.", "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // Ajustar a largura das colunas
    TableColumn checkboxColumn = tabelaVendas.getColumnModel().getColumn(0);
    checkboxColumn.setPreferredWidth(30);  // Ajustar a largura da coluna de checkbox
    checkboxColumn.setMaxWidth(30);
    checkboxColumn.setMinWidth(30);

    TableColumn vendaIdColumn = tabelaVendas.getColumnModel().getColumn(1);
    vendaIdColumn.setPreferredWidth(50);   // Ajustar a largura da coluna "ID"
    vendaIdColumn.setMaxWidth(50);         // Definir largura máxima para "ID"
    vendaIdColumn.setMinWidth(50);         // Definir largura mínima para "ID"

    // Adicionar o MouseListener para o clique duplo
    tabelaVendas.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && !e.isConsumed()) { // Verifica se é um clique duplo e evita múltiplos eventos
                e.consume(); // Marca o evento como consumido para evitar duplicação

                int row = tabelaVendas.getSelectedRow(); // Obter a linha selecionada
                if (row != -1) {
                    // Obter os dados da linha selecionada
                    String vendaId = tabelaVendas.getValueAt(row, 1).toString(); // ID da venda
                    String cliente = tabelaVendas.getValueAt(row, 2).toString(); // Nome do cliente
                    String produtos = tabelaVendas.getValueAt(row, 10).toString(); // Produtos vendidos

                    // Exibir os detalhes da venda em um JDialog (não incluímos o "Estado" aqui)
                    mostrarDetalhesVenda(vendaId, cliente, produtos);
                }
            }
        }
    });
}

// Método auxiliar para extrair valores
private String obterValorDaLinha(String linha, String campo) {
    if (linha == null || !linha.contains(":")) {
        throw new IllegalArgumentException("Linha mal formatada para o campo: " + campo);
    }
    return linha.split(":", 2)[1].trim();
}

// Método para exibir os detalhes da venda
private void mostrarDetalhesVenda(String vendaId, String cliente, String produtos) {
    // Criar o JDialog para exibir os detalhes da venda
    JDialog dialog = new JDialog();
    dialog.setTitle("Detalhes da Venda");
    dialog.setSize(500, 400);
    dialog.setLocationRelativeTo(null); // Centralizar

    // Criar um painel para o JDialog
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    // Informações do cliente
    panel.add(new JLabel("Venda ID: " + vendaId));
    panel.add(new JLabel("Cliente: " + cliente));

    // Adicionar uma tabela para os produtos vendidos
    String[] colunas = {"Produto", "Quantidade", "Valor Unitário", "Valor Total"};
    DefaultTableModel modelProdutos = new DefaultTableModel(null, colunas);
    JTable tabelaProdutos = new JTable(modelProdutos);
    JScrollPane scrollPane = new JScrollPane(tabelaProdutos);
    panel.add(new JLabel("Produtos Vendidos:"));
    panel.add(scrollPane); // Adiciona o JScrollPane com a tabela

    // Preencher a tabela com os produtos extraídos
    String[] produtosArray = produtos.split("; ");
    for (String produto : produtosArray) {
        // Assumimos que os dados de produto estão formatados como:
        // "Produto: NOME | Quantidade: N | Valor Unitário: X"
        String[] produtoDetails = produto.split("\\|");
        String nomeProduto = produtoDetails[0].split(":")[1].trim();
        String quantidade = produtoDetails[1].split(":")[1].trim();
        String valorUnitario = produtoDetails[2].split(":")[1].trim();

        // Calcular o valor total (quantidade * valor unitário)
        double valorUnitarioDouble = Double.parseDouble(valorUnitario);
        int quantidadeInt = Integer.parseInt(quantidade);
        double valorTotal = valorUnitarioDouble * quantidadeInt;

        // Adicionar as informações do produto na tabela
        modelProdutos.addRow(new Object[]{nomeProduto, quantidade, valorUnitario, String.format("R$ %.2f", valorTotal)});
    }

    // Adicionar um botão para fechar o JDialog
    JButton fecharButton = new JButton("Fechar");
    fecharButton.addActionListener(e -> dialog.dispose());
    panel.add(fecharButton);

    // Adicionar o painel ao JDialog
    dialog.add(panel);
    dialog.setVisible(true); // Exibir o JDialog
}

private void atualizarArquivoVendas() {
    // Caminho do arquivo onde as vendas são salvas
    String arquivoVendas = "Vendas.txt";

    // Obter o modelo da tabela
    DefaultTableModel model = (DefaultTableModel) tabela_vendas.getModel();

    // Tentar escrever no arquivo
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoVendas))) {
        // Itera sobre as linhas da tabela
        for (int i = 0; i < model.getRowCount(); i++) {
            String vendaId = (String) model.getValueAt(i, 1);  // Coluna ID
            String cliente = (String) model.getValueAt(i, 2);  // Coluna Cliente
            String endereco = (String) model.getValueAt(i, 3); // Coluna Endereço
            String numero = (String) model.getValueAt(i, 4);   // Coluna Número
            String bairro = (String) model.getValueAt(i, 5);    // Coluna Bairro
            String contato = (String) model.getValueAt(i, 6);   // Coluna Contato
            String dataEntrega = (String) model.getValueAt(i, 7); // Coluna Data de Entrega
            String pagamento = (String) model.getValueAt(i, 8); // Coluna Pagamento
            String valorTotal = (String) model.getValueAt(i, 9); // Coluna Valor Total
            String estado = (String) model.getValueAt(i, 11);   // Coluna Estado (seja "ENTREGUE" ou não)

            // Escreve os dados no arquivo
            writer.write("Venda ID: " + vendaId + "\n");
            writer.write("Cliente: " + cliente + "\n");
            writer.write("Endereço: " + endereco + "\n");
            writer.write("Número: " + numero + "\n");
            writer.write("Bairro: " + bairro + "\n");
            writer.write("Contato: " + contato + "\n");
            writer.write("Data de Entrega: " + dataEntrega + "\n");
            writer.write("Pagamento: " + pagamento + "\n");
            writer.write("Valor Total: " + valorTotal + "\n");
            writer.write("Estado: " + estado + "\n");

            // Se houver produtos vendidos
            String produtos = (String) model.getValueAt(i, 10); // Coluna Produtos Vendidos
            if (produtos != null && !produtos.isEmpty()) {
                writer.write("Produto: " + produtos + "\n");
            }

            writer.write("\n"); // Linha em branco para separar as vendas
        }

    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erro ao salvar os dados das vendas.", "Erro", JOptionPane.ERROR_MESSAGE);
    }
}



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BAIRRO;
    private javax.swing.JButton Button_cadastrarcliente;
    private javax.swing.JButton Button_cancelar_cliente;
    private javax.swing.JButton Button_castraritem;
    private javax.swing.JButton Button_consultarclientes;
    private javax.swing.JButton Button_consultarvenda;
    private javax.swing.JButton Button_desconectar;
    private javax.swing.JButton Button_editar_usuarios;
    private javax.swing.JButton Button_inventario;
    private javax.swing.JButton Button_limparFiltros;
    private javax.swing.JButton Button_novavenda;
    private javax.swing.JButton Button_salvar_cliente;
    private javax.swing.JTextField Cliente_bairro;
    private javax.swing.JTextField Cliente_contato;
    private javax.swing.JTextField Cliente_endereco;
    private javax.swing.JTextField Cliente_ncasa;
    private javax.swing.JTextField Cliente_nome;
    private javax.swing.JTextArea Cliente_obs;
    private javax.swing.JTextField Cliente_pontoref;
    private javax.swing.JComboBox<String> Combobox_pagamento;
    private javax.swing.JButton Confirmar_venda;
    private javax.swing.JTable Inventario_tabela;
    private javax.swing.JComboBox<String> Item_cadastrouser_item;
    private javax.swing.JButton Item_cancelar_item;
    private javax.swing.JTextField Item_data_item;
    private javax.swing.JButton Item_delete;
    private javax.swing.JTextArea Item_descricao_item;
    private javax.swing.JComboBox<String> Item_marca_item;
    private javax.swing.JTextField Item_nome_item;
    private javax.swing.JSpinner Item_quantidade_item;
    private javax.swing.JButton Item_salvar_item;
    private javax.swing.JComboBox<String> Item_tipo_item;
    private javax.swing.JFormattedTextField Item_valor_unidade_item;
    private javax.swing.JTextField Item_valortotal_item;
    private javax.swing.JPanel Menu_clientes_selecao;
    private javax.swing.JPanel Menu_clientes_tela;
    private javax.swing.JPanel Menu_config;
    private javax.swing.JPanel Menu_itens_selecao;
    private javax.swing.JPanel Menu_itens_tela;
    private javax.swing.JTabbedPane Menu_vendas;
    private javax.swing.JPanel Menu_vendas_selecao;
    private javax.swing.JPanel Menu_vendas_tela;
    private javax.swing.JTextField Pesquisar_cliente;
    private javax.swing.JTextField Pesquisar_venda;
    private javax.swing.JTextField Table_pesquisar;
    private javax.swing.JPanel Tela_clientes;
    private javax.swing.JPanel Tela_consultar_vendas;
    private javax.swing.JPanel Tela_inventario;
    private javax.swing.JPanel Tela_nova_venda;
    private javax.swing.JPanel Tela_novo_cliente;
    private javax.swing.JPanel Tela_novo_item;
    private javax.swing.JTextField Usuario_on;
    private javax.swing.JButton button_adicionar_listacompra;
    private javax.swing.JButton button_entregar;
    private javax.swing.JButton button_excluir_venda;
    private javax.swing.JComboBox<String> combobox_venda_cliente;
    private javax.swing.JButton excluir_cliente;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JButton organiza_bairro;
    private javax.swing.JButton organiza_clientes_venda;
    private javax.swing.JButton organiza_data;
    private javax.swing.JButton organiza_endereco;
    private javax.swing.JButton organiza_entregue_venda;
    private javax.swing.JButton organiza_item;
    private javax.swing.JButton organiza_marca;
    private javax.swing.JButton organiza_nome;
    private javax.swing.JButton organiza_pendente_venda;
    private javax.swing.JButton organiza_tipo;
    private javax.swing.JButton remover_itens;
    private javax.swing.JTable tabelaClientes;
    private javax.swing.JTable tabela_vendas;
    private javax.swing.JTextField venda_bairro;
    private javax.swing.JTextField venda_contato;
    private javax.swing.JTextField venda_dataentrega;
    private javax.swing.JTextField venda_desconto;
    private javax.swing.JTextField venda_endereco;
    private javax.swing.JTextField venda_numcasa;
    private javax.swing.JTable venda_tabela_itens;
    private javax.swing.JTable venda_tabela_listacompra;
    private javax.swing.JTextField venda_valor;
    private javax.swing.JTextField venda_valor_total;
    // End of variables declaration//GEN-END:variables

}

