package dimyoux.engine.utils.parsers;

import java.util.ArrayList;
import java.util.List;

import dimyoux.engine.scene.Mesh;
import dimyoux.engine.utils.Buffer;
import dimyoux.engine.utils.Log;
import dimyoux.engine.utils.math.Coord2D;
import dimyoux.engine.utils.math.Coord3D;

public class PreMesh
{
	public List<Coord3D> vertices;
	public List<Coord3D> normals;
	public List<Coord2D> textureCoordinates;
	public List<Face> faces;
	public String name;
	public PreMesh()
	{
		vertices = new ArrayList<Coord3D>();
		normals = new ArrayList<Coord3D>();
		textureCoordinates = new ArrayList<Coord2D>();
		faces = new ArrayList<Face>();
	}
	@Override
	public String toString()
	{
		String txt = "[Mesh";
		if(name != null)
		{
			txt+="["+name+"]";
		}
		if(vertices.size()>0)
		{
			txt+=" V=\""+vertices.size()+"\"";
		}
		if(normals.size()>0)
		{
			txt+=" N=\""+normals.size()+"\"";
		}
		if(textureCoordinates.size()>0)
		{
			txt+=" T=\""+textureCoordinates.size()+"\"";
		}
		if(faces.size()>0)
		{
			txt+=" F=\""+faces.size()+"\"";
		}
		return txt+"]";
	}
	public Mesh toMesh()
	{
		Mesh mesh = new Mesh();
		if(vertices.size()>0)
		{
			mesh.verticesBuffer = Buffer.CreateFloatBuffer(vertices.size()*3);
			for(final Coord3D vertex : vertices)
			{
				mesh.verticesBuffer.put(vertex.x);
				mesh.verticesBuffer.put(vertex.y);
				mesh.verticesBuffer.put(vertex.z);
			}
		}
		if(normals.size()>0)
		{
			mesh.normalsBuffer = Buffer.CreateFloatBuffer(normals.size()*3);
			for(final Coord3D normal : normals)
			{
				mesh.normalsBuffer.put(normal.x);
				mesh.normalsBuffer.put(normal.y);
				mesh.normalsBuffer.put(normal.z);
			}
		}
		if(textureCoordinates.size()>0)
		{
			mesh.texCoordsBuffer = Buffer.CreateFloatBuffer(textureCoordinates.size()*2);
			for(final Coord2D text : textureCoordinates)
			{
				mesh.texCoordsBuffer.put(text.u);
				mesh.texCoordsBuffer.put(text.v);
			}
		}
		if(faces.size()>0)
		{
			mesh.indexesBuffer = Buffer.createIntBuffer(faces.size()*faces.get(0).size());
			for(final Face face : faces)
			{
				for(final Integer index : face.vertices)
				{
					mesh.indexesBuffer.put(index-1);
				}
			}
		}
		return mesh;
	}
}