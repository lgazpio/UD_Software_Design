package SOLID._2O;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static jdk.nashorn.internal.runtime.regexp.joni.Syntax.Java;

public class O
{
	/*  Open-Closed Principle
	 * 
	 *  Generic implementations are preferred for classes. 
	 *  Open for generic use / extension, closed for modification
	 * 
	 * */
	
	public static void main(String [] args )
	{
		Product apple = new Product("Apple", Color.GREEN, Size.SMALL);
		Product tree = new Product("Tree", Color.GREEN, Size.LARGE);
		Product house = new Product("House", Color.BLUE, Size.LARGE);
		
		// Java 9 for the List.of
		//List<Product> products = List.of(apple, tree, house);

        // Java 8
        List<Product> products = new ArrayList<Product>();
        products.add(apple);
        products.add(tree);
        products.add(house);

        ProductFilter pf = new ProductFilter();
		System.out.println("Green products (BAD implementation):");
		pf.filterByColor(products, Color.GREEN).forEach(p -> System.out.println(" - " + p.name + " is green"));
				
		BetterFilter bf = new BetterFilter();
		System.out.println("Green products (GOOD implementation):");
		bf.filter(products, new ColorSpecification(Color.GREEN)).forEach(p -> System.out.println(" - " + p.name + " is green"));
		
		System.out.println("Large blue items (GOOD implementation):");
		bf.filter(products, 
				new AndSpecification <> (
						new ColorSpecification(Color.BLUE),
						new SizeSpecification(Size.LARGE)
						)).forEach(p -> System.out.println(" - " + p.name + " is large and blue"));				
	}	
}

enum Color
{
	RED, GREEN, BLUE
}

enum Size
{
	SMALL, MEDIUM, LARGE, HUGE
}

class Product
{
	public String name;
	public Color color;
	public Size size;
	
	public Product(String name, Color color, Size size) 
	{
		this.name = name;
		this.color = color;
		this.size = size;
	}
}


class ProductFilter
{
	public Stream<Product> filterByColor(List<Product> products, Color color)
	{
		return products.stream().filter(p -> p.color == color);
	}
	
	
	public Stream<Product> filterBySize(List<Product> products, Size size)
	{
		return products.stream().filter(p -> p.size == size);
	}
	
	
	public Stream<Product> filterBySizeAndColor( List<Product> products, Size size, Color color)
	{
		return products.stream().filter(p -> p.size == size && p.color == color);
	}
	
	// Cada vez que necesitemos un nuevo filtro tendremos que hacer un nuevo copy-paste --> esto no es escalable! y no conviene alterar el código de una clase que era ya funcional !
}




// Patron de diseño Specification

interface Specification<T>
{
	boolean isSatisfied(T item);
}


interface Filter<T>
{
	Stream <T> filter(List<T> items, Specification<T> spec);
}

class ColorSpecification implements Specification<Product>
{
	private Color color;
	
	public ColorSpecification(Color color) 
	{
		this.color = color;
	}
	
	@Override
	public boolean isSatisfied(Product item) 
	{
		return item.color == color;
	}
}


class SizeSpecification implements Specification<Product>
{
	private Size size;
	
	public SizeSpecification(Size size) 
	{
		this.size = size;
	}
	
	@Override
	public boolean isSatisfied(Product item) 
	{
		return item.size == size;
	}
}


class BetterFilter implements Filter<Product>
{

	@Override
	public Stream<Product> filter(List<Product> items, Specification<Product> spec) 
	{
		return items.stream().filter(p -> spec.isSatisfied(p));
	}
	
}


class AndSpecification <T> implements Specification<T>
{
	private Specification<T> first, second;
	
	public AndSpecification(Specification <T> first, Specification <T> second)
	{
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean isSatisfied(T item) 
	{
		return first.isSatisfied(item) && second.isSatisfied(item);
	}
	
}
