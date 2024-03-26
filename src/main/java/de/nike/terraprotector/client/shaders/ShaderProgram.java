package de.nike.terraprotector.client.shaders;

import de.nike.terraprotector.lib.BufferUtils;
import net.minecraft.world.level.levelgen.structure.StructureFeatureIndexSavedData;
import org.lwjgl.opengl.GL20;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;

public class ShaderProgram {

    private String log = "";
    private boolean isCompiled;
    private String[] uniformNames;
    private String[] attributeNames;
    private int program;
    private int vertexShaderHandle;
    private int fragmentShaderHandle;
    private final String vertexShaderSource;
    private final String fragmentShaderSource;

    private final HashMap<String, Integer> uniforms = new HashMap<>();
    private final HashMap<String, Integer> uniformTypes = new HashMap<>();
    private final HashMap<String, Integer> uniformSizes = new HashMap<>();

    private final HashMap<String, Integer> attributes = new HashMap<>();
    private final HashMap<String, Integer> attributeTypes = new HashMap<>();
    private final HashMap<String, Integer> attributeSizes = new HashMap<>();

    public ShaderProgram (String vertexShader, String fragmentShader) {
        if (vertexShader == null) throw new IllegalArgumentException("vertex shader must not be null");
        if (fragmentShader == null) throw new IllegalArgumentException("fragment shader must not be null");

        this.vertexShaderSource = vertexShader;
        this.fragmentShaderSource = fragmentShader;
    }

    IntBuffer params = BufferUtils.newIntBuffer(1);
    IntBuffer type = BufferUtils.newIntBuffer(1);

    public String getVertexShaderSource () {
        return vertexShaderSource;
    }
    public String getFragmentShaderSource () {
        return fragmentShaderSource;
    }

    boolean test = false;

    public void bind() {
        if(!isCompiled) {
            compileShaders(vertexShaderSource, fragmentShaderSource);
            if (isCompiled()) {
                fetchUniforms();
                fetchAttributes();
            } else if(!test) {
                System.err.println("Error compiling shader!");
                System.err.println(getLog());
                test = true;
            } else return;
        }
        GL20.glUseProgram(program);
    }

    public void release() {
        GL20.glUseProgram(0);
    }

    public void setUniformf(String name, float value) {
        int location = fetchUniformLocation(name);
        GL20.glUniform1f(location, value);
    }

    public void setUniformf(String name, float value1, float value2) {
        int location = fetchUniformLocation(name);
        GL20.glUniform2f(location, value1, value2);
    }

    public void setUniformf(String name, float value1, float value2, float value3) {
        int location = fetchUniformLocation(name);
        GL20.glUniform3f(location, value1, value2, value3);
    }

    public void setUniformf(String name, float value1, float value2, float value3, float value4) {
        int location = fetchUniformLocation(name);
        GL20.glUniform4f(location, value1, value2, value3, value4);
    }

    public void setUniformi(String name, int value) {
        int location = fetchUniformLocation(name);
        GL20.glUniform1i(location, value);
    }

    public void setUniformi(String name, int value1, int value2) {
        int location = fetchUniformLocation(name);
        GL20.glUniform2i(location, value1, value2);
    }

    public void setUniformi(String name, int value1, int value2, int value3) {
        int location = fetchUniformLocation(name);
        GL20.glUniform3i(location, value1, value2, value3);
    }

    public int fetchUniformLocation(String name) {
        return fetchUniformLocation(name, false);
    }

    public int fetchUniformLocation (String name, boolean pedantic) {
        int location;
        if ((location = uniforms.getOrDefault(name, -2)) == -2) {
            location = GL20.glGetUniformLocation(program, name);
            if (location == -1 && pedantic) {
                if (isCompiled) throw new IllegalArgumentException("No uniform with name '" + name + "' in shader");
                throw new IllegalStateException("An attempted fetch uniform from uncompiled shader \n" + getLog());
            }
            uniforms.put(name, location);
        }
        return location;
    }


    private void fetchUniforms() {
        ((Buffer)params).clear();
        GL20.glGetProgramiv(program, GL20.GL_ACTIVE_UNIFORMS, params);
        int numUniforms = params.get(0);
        uniformNames = new String[numUniforms];
        for (int i = 0; i < numUniforms; i++) {
            ((Buffer)params).clear();
            params.put(0, 1);
            ((Buffer)type).clear();
            String name = GL20.glGetActiveUniform(program, i, params, type);
            int location = GL20.glGetUniformLocation(program, name);
            uniforms.put(name, location);
            uniformTypes.put(name, type.get(0));
            uniformSizes.put(name, params.get(0));
            uniformNames[i] = name;
        }
    }

    private void fetchAttributes() {
        ((Buffer)params).clear();
        GL20.glGetProgramiv(program, GL20.GL_ACTIVE_ATTRIBUTES, params);
        int numAttributes = params.get(0);

        attributeNames = new String[numAttributes];

        for (int i = 0; i < numAttributes; i++) {
            ((Buffer)params).clear();
            params.put(0, 1);
            ((Buffer)type).clear();
            String name = GL20.glGetActiveAttrib(program, i, params, type);
            int location = GL20.glGetAttribLocation(program, name);
            attributes.put(name, location);
            attributeTypes.put(name, type.get(0));
            attributeSizes.put(name, params.get(0));
            attributeNames[i] = name;
        }
    }

    private void compileShaders (String vertexShader, String fragmentShader) {
        vertexShaderHandle = loadShader(GL20.GL_VERTEX_SHADER, vertexShader);
        fragmentShaderHandle = loadShader(GL20.GL_FRAGMENT_SHADER, fragmentShader);

        if (vertexShaderHandle == -1 || fragmentShaderHandle == -1) {
            isCompiled = false;
            return;
        }

        program = linkProgram(createProgram());
        if (program == -1) {
            isCompiled = false;
            return;
        }

        isCompiled = true;
    }

    protected int createProgram () {
        int program = GL20.glCreateProgram();
        return program != 0 ? program : -1;
    }

    private int loadShader (int type, String source) {
        IntBuffer intbuf = BufferUtils.newIntBuffer(1);

        int shader = GL20.glCreateShader(type);
        if (shader == 0) return -1;

        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        GL20.glGetShaderiv(shader, GL20.GL_COMPILE_STATUS, intbuf);

        int compiled = intbuf.get(0);
        if (compiled == 0) {

            String infoLog = GL20.glGetShaderInfoLog(shader);
            log += type == GL20.GL_VERTEX_SHADER ? "Vertex shader\n" : "Fragment shader:\n";
            log += infoLog;
            return -1;
        }

        return shader;
    }

    public String getLog () {
        if (isCompiled) {
            log = GL20.glGetProgramInfoLog(program);
            return log;
        } else {
            return log;
        }
    }

    public boolean isCompiled () {
        return isCompiled;
    }

    public boolean hasUniform (String name) {
        return uniforms.containsKey(name);
    }
    public int getUniformType (String name) {
        return uniformTypes.getOrDefault(name, 0);
    }
    public int getUniformLocation (String name) {
        return uniforms.getOrDefault(name, -1);
    }
    public int getUniformSize (String name) {
        return uniformSizes.getOrDefault(name, 0);
    }

    private int linkProgram (int program) {
        if (program == -1) return -1;

        GL20.glAttachShader(program, vertexShaderHandle);
        GL20.glAttachShader(program, fragmentShaderHandle);
        GL20.glLinkProgram(program);

        ByteBuffer tmp = ByteBuffer.allocateDirect(4);
        tmp.order(ByteOrder.nativeOrder());
        IntBuffer intbuf = tmp.asIntBuffer();

        GL20.glGetProgramiv(program, GL20.GL_LINK_STATUS, intbuf);
        int linked = intbuf.get(0);
        if (linked == 0) {
            log = GL20.glGetProgramInfoLog(program);
            return -1;
        }

        return program;
    }

    public void dispose() {
        GL20.glUseProgram(0);
        GL20.glDeleteShader(vertexShaderHandle);
        GL20.glDeleteShader(fragmentShaderHandle);
        GL20.glDeleteProgram(program);
    }

}
