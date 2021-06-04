package Server.DAL;

public class DeleteProductDTO extends ProductDTO{
    private ProductDTO dto;

    public DeleteProductDTO(){

    }

    public DeleteProductDTO(ProductDTO dto){
        this.dto = dto;
    }

    @Override
    public boolean toDelete(){
        return true;
    }

    @Override
    public ProductDTO getDTO(){
        return this.dto;
    }

}
